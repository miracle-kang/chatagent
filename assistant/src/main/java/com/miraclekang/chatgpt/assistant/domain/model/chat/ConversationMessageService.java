package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.exception.DomainException;
import com.miraclekang.chatgpt.assistant.domain.EnumExceptionCode;
import com.miraclekang.chatgpt.assistant.domain.model.billing.Token;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenCharge;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenChargeService;
import com.miraclekang.chatgpt.assistant.domain.model.equity.UserEquityChecker;
import com.miraclekang.chatgpt.assistant.domain.model.equity.UserEquityCheckerProvider;
import com.miraclekang.chatgpt.common.reactive.Requester;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
public class ConversationMessageService {

    private final ChatService chatService;
    private final TokenChargeService chargeService;
    private final UserEquityCheckerProvider equityCheckerProvision;

    private final ConversationMessageRepository messageRepository;

    private final String defaultSystemMessage;
    private final Set<String> blockTopics = Set.of("New Chat", "新建会话", "新建聊天");

    public ConversationMessageService(ChatService chatService,
                                      TokenChargeService chargeService,
                                      UserEquityCheckerProvider equityCheckerProvision,
                                      ConversationMessageRepository messageRepository,
                                      @Value("${third-party.openai.system-message:none}") String systemMessage) {
        this.chatService = chatService;
        this.equityCheckerProvision = equityCheckerProvision;
        this.messageRepository = messageRepository;
        this.chargeService = chargeService;

        this.defaultSystemMessage = systemMessage;
    }


    public Flux<Message> sendConversionMessage(Conversation conversation,
                                               Requester requester,
                                               ConversationMessage sendingMessage) {

        MessageConfig messageConfig = conversation.messageConfig();
        UserEquityChecker equityChecker = equityCheckerProvision.provision(requester, conversation.getModel());

        return Mono.just(conversation.getConversationId())
                .publishOn(Schedulers.boundedElastic())
                .filter(messages -> equityChecker.allowUseModel(conversation.getModel()))
                .switchIfEmpty(Mono.error(new DomainException("Not allowed to use model " + conversation.getModel(),
                        EnumExceptionCode.Forbidden)))
                .map(conversationId -> {
                    List<Message> historyMessages = List.of();
                    if (BooleanUtils.isNotFalse(conversation.getSendHistory())) {
                        historyMessages = messageRepository.tail(conversationId, 20, MessageStatus.Succeeded)
                                .stream().map(ConversationMessage::getMessage).toList();
                    }
                    return Stream.concat(historyMessages.stream(), Stream.of(sendingMessage.getMessage()))
                            .toList();
                })
                // Ensure message max tokens
                .map(messages -> ensureMessageMaxTokens(
                        conversation.getName(),
                        messageConfig.getSystemMessage(),
                        messages,
                        messageConfig.getModel(),
                        equityChecker.allowedMaxToken(messageConfig.getModel(), messageConfig.getMaxTokens())))
                // Check if the requester could use token
                .filter(messages -> equityChecker.couldUseToken(messageConfig.getModel(), messages.stream()
                        .map(message -> message.encodeToken(messageConfig.getModel()))
                        .toList()))
                .switchIfEmpty(Mono.error(new DomainException("Not enough token", EnumExceptionCode.Forbidden)))
                .flatMapMany(messages -> {
                    List<Message> replyMessages = new ArrayList<>();
                    return chatService.fluxSendMessages(messages, messageConfig)
                            .publishOn(Schedulers.boundedElastic())
                            .doFirst(() -> {
                                TokenCharge charge = chargeService.chargeTokens(requester, conversation.getModel(),
                                        messages.stream().map(msg -> msg.encodeToken(conversation.getModel()))
                                                .toList());
                                log.debug(">>>>>> Chat message start, sent messages {}, charged token {}",
                                        messages.size(), charge.getTokens());
                                // Context size = messages - systemMessage - sendingMessage
                                sendingMessage.success(messages.size() - 2, charge.getTokens());
                                messageRepository.save(sendingMessage);
                            })
                            .doOnNext(replyMessage -> {
                                log.trace(">>>>>> Chat Message: {}", replyMessage);
                                replyMessages.add(replyMessage);
                            })
                            .doOnError(err -> {
                                log.error("Error on sent message", err);
                                if (!replyMessages.isEmpty()) {
                                    chargeService.chargeToken(requester, conversation.getModel(),
                                            Message.ofAssistant(replyMessages).encodeToken(conversation.getModel()));
                                }
                            })
                            .doOnComplete(() -> {
                                if (replyMessages.isEmpty()) {
                                    log.warn("No reply message");
                                    return;
                                }
                                var replyMessage = Message.ofAssistant(replyMessages);
                                TokenCharge charge = chargeService.chargeToken(requester, conversation.getModel(),
                                        replyMessage.encodeToken(conversation.getModel()));

                                var replyConversationMsg = conversation.newMessage(replyMessage);
                                replyConversationMsg.success(messages.size() - 1, charge.getTokens());
                                messageRepository.save(replyConversationMsg);

                                log.debug(">>>>>> Chat message complete, reply: {}", replyMessage);
                            });
                });
    }

    private List<Message> ensureMessageMaxTokens(String conversationName,
                                                 String strSystemMessage,
                                                 List<Message> messages,
                                                 ChatModel model, int maxToken) {
        if (messages.isEmpty()) {
            throw new IllegalArgumentException("Message is empty.");
        }
        Message systemMessage;
        if (strSystemMessage != null) {
            systemMessage = Message.ofSystem(strSystemMessage);
        } else {
            String topic = StringUtils.isEmpty(conversationName) || blockTopics.contains(conversationName)
                    ? null : conversationName;
            if (topic != null) {
                systemMessage = Message.ofSystem(defaultSystemMessage
                        + "\nThe topic is about " + topic);
            } else {
                systemMessage = Message.ofSystem(defaultSystemMessage);
            }
        }
        Message latestMessage = messages.get(messages.size() - 1);

        Token systemMessageToken = systemMessage.encodeToken(model);
        Token newMessageToken = latestMessage.encodeToken(model);

        Token tokenUsed = systemMessageToken.add(newMessageToken);
        if (tokenUsed.numTokens() > maxToken) {
            throw new IllegalArgumentException("Message over the max token limit.");
        }

        List<Message> chatMessages = new ArrayList<>(messages.size());
        chatMessages.add(latestMessage);

        for (int i = messages.size() - 2; i >= 0; i--) {
            Message message = messages.get(i);
            Token messageToken = message.encodeToken(model);
            tokenUsed = tokenUsed.add(messageToken);
            if (tokenUsed.numTokens() > maxToken) {
                break;
            }
            chatMessages.add(message);
        }
        chatMessages.add(systemMessage);
        Collections.reverse(chatMessages);

        return chatMessages;
    }

}
