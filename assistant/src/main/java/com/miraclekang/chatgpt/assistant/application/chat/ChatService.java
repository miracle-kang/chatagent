package com.miraclekang.chatgpt.assistant.application.chat;

import com.miraclekang.chatgpt.assistant.application.chat.command.NewConversationCommand;
import com.miraclekang.chatgpt.assistant.application.chat.command.NewMessageCommand;
import com.miraclekang.chatgpt.assistant.application.chat.querystack.ConversationArchiveDTO;
import com.miraclekang.chatgpt.assistant.application.chat.querystack.ConversationDTO;
import com.miraclekang.chatgpt.assistant.application.chat.querystack.MessageDTO;
import com.miraclekang.chatgpt.assistant.application.chat.querystack.MessagePartialDTO;
import com.miraclekang.chatgpt.assistant.domain.model.chat.*;
import com.miraclekang.chatgpt.assistant.domain.model.equity.UserEquityCheckerProvider;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.reactive.Requester;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class ChatService {

    private final ConversationMessageService messageService;
    private final UserEquityCheckerProvider equityCheckerProvider;
    private final ConversationProvisionService conversationProvisionService;

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;
    private final ConversationArchiveRepository conversationArchiveRepository;

    public ChatService(ConversationMessageService messageService,
                       UserEquityCheckerProvider equityCheckerProvider,
                       ConversationProvisionService conversationProvisionService,
                       ConversationRepository conversationRepository,
                       ConversationMessageRepository conversationMessageRepository,
                       ConversationArchiveRepository conversationArchiveRepository) {
        this.messageService = messageService;
        this.equityCheckerProvider = equityCheckerProvider;
        this.conversationProvisionService = conversationProvisionService;
        this.conversationRepository = conversationRepository;
        this.conversationMessageRepository = conversationMessageRepository;
        this.conversationArchiveRepository = conversationArchiveRepository;
    }

    @Transactional
    public Mono<ConversationDTO> newConversation(NewConversationCommand command) {
        return Requester.currentRequester().publishOn(Schedulers.boundedElastic())
                .flatMap(requester -> conversationProvisionService.provisionConversation(
                        requester,
                        command.getName(),
                        command.getModel(),
                        command.getSendHistory(),
                        command.getTemperature(),
                        command.getTopP(),
                        command.getMaxTokens()))
                .flatMap(conversation -> blockingOperation(
                        () -> ConversationDTO.of(conversationRepository.save(conversation))));
    }

    @Transactional
    public Mono<ConversationDTO> updateConversation(String aConversationId, NewConversationCommand command) {
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(new ConversationId(aConversationId))
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(conversationId -> blockingOperation(() -> conversationRepository
                                .findByConversationIdAndOwnerUserId(conversationId, new UserId(requester.getUserId()))))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not exists")))
                        .flatMap(conversation -> equityCheckerProvider.provision(requester, command.getModel())
                                .flatMap(checker -> {
                                    conversation.update(
                                            command.getName(),
                                            command.getModel(),
                                            command.getSendHistory(),
                                            command.getTemperature(),
                                            command.getTopP(),
                                            command.getMaxTokens(),
                                            checker
                                    );
                                    return blockingOperation(() -> conversationRepository.save(conversation));
                                })
                        ).map(ConversationDTO::of));
    }

    public Mono<ConversationDTO> getConversation(String aConversationId) {
        ConversationId conversationId = new ConversationId(aConversationId);
        return Requester.currentRequester().publishOn(Schedulers.boundedElastic())
                .mapNotNull(Requester::getUserId)
                .flatMap(userId -> blockingOperation(() -> conversationRepository
                        .findByConversationIdAndOwnerUserId(conversationId, new UserId(userId))))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not exists")))
                .map(ConversationDTO::of);
    }

    public Mono<ConversationDTO> enabledConversationSendHistory(String aConversationId, Boolean enabled) {
        ConversationId conversationId = new ConversationId(aConversationId);
        return Requester.currentRequester().publishOn(Schedulers.boundedElastic())
                .mapNotNull(Requester::getUserId)
                .flatMap(userId -> blockingOperation(() -> conversationRepository
                        .findByConversationIdAndOwnerUserId(conversationId, new UserId(userId))))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not exists")))
                .flatMap(conversation -> {
                    conversation.enabledSendHistory(enabled);
                    return blockingOperation(() -> ConversationDTO.of(conversationRepository.save(conversation)));
                });
    }

    @Transactional
    public Mono<Void> deleteConversation(String aConversationId) {
        ConversationId conversationId = new ConversationId(aConversationId);
        return Requester.currentRequester().publishOn(Schedulers.boundedElastic())
                .mapNotNull(Requester::getUserId)
                .flatMap(userId -> blockingOperation(() -> conversationRepository
                        .findByConversationIdAndOwnerUserId(conversationId, new UserId(userId))))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not exists")))
                .flatMap(conversation -> Mono.just(conversation.archive())
                        .flatMap(archive -> blockingOperation(() -> {
                            conversationArchiveRepository.save(archive);
                            conversationRepository.delete(conversation);
                        })));
    }

    public Mono<Page<ConversationDTO>> queryConversations(Pageable pageable) {
        return Requester.currentRequester().publishOn(Schedulers.boundedElastic())
                .flatMap(requester -> {
                    Specification<Conversation> userConversation = ((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get(Conversation_.ownerUserId), new UserId(requester.getUserId())));
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Conversation_.ID).descending());

                    return blockingOperation(() -> conversationRepository.findAll(userConversation, pageRequest)
                            .map(ConversationDTO::of));
                });
    }

    public Flux<MessagePartialDTO> newConversationMessage(String aConversationId, NewMessageCommand command) {
        return Requester.currentRequester()
                .flatMapMany(requester -> Mono.just(new UserId(requester.getUserId()))
                        .flatMap(userId -> blockingOperation(() -> conversationRepository.findByConversationIdAndOwnerUserId(
                                new ConversationId(aConversationId), userId)))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not found.")))
                        .flatMapMany(conversation -> Mono.just(conversation
                                        .newMessage(Message.ofUser(command.getContent())))
                                .publishOn(Schedulers.boundedElastic())
                                .flatMap(message -> blockingOperation(() -> conversationMessageRepository.save(message)))
                                .flatMapMany(sendingMessage -> conversation.sendMessage(messageService,
                                                requester, sendingMessage)
                                        .publishOn(Schedulers.boundedElastic())
                                        .doOnTerminate(() -> conversationMessageRepository.save(sendingMessage)))))
                .filter(message -> Objects.nonNull(message.getContent()))
                .map(MessagePartialDTO::of);
    }

    public Mono<Page<MessageDTO>> queryConversationMessages(String aConversationId, Pageable pageable) {
        ConversationId conversationId = new ConversationId(aConversationId);

        return Requester.currentRequester()
                .flatMap(requester -> blockingOperation(() -> conversationRepository
                        .findByConversationIdAndOwnerUserId(conversationId, new UserId(requester.getUserId()))))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not found.")))
                .flatMap(conversation -> conversationMessages(conversation.getConversationId(), pageable));
    }

    @Transactional
    public Mono<Void> clearConversationMessages(String aConversationId) {
        ConversationId conversationId = new ConversationId(aConversationId);
        return Requester.currentRequester()
                .flatMap(requester -> blockingOperation(() -> conversationRepository
                        .existsByConversationIdAndOwnerUserId(conversationId, new UserId(requester.getUserId()))))
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not found.")))
                .flatMap(conversation -> {
                    conversationMessageRepository.clearMessages(conversationId);
                    return Mono.empty();
                });
    }

    @Transactional
    public Mono<Void> deleteMessage(String aConversationId, String aMessageId) {
        Objects.requireNonNull(aConversationId, "ConversationId must not be null");
        Objects.requireNonNull(aMessageId, "MessageId must not be null");

        ConversationId conversationId = new ConversationId(aConversationId);
        ConversationMessageId messageId = new ConversationMessageId(aMessageId);

        return Requester.currentRequester()
                .flatMap(requester -> blockingOperation(() -> conversationRepository
                        .existsByConversationIdAndOwnerUserId(conversationId, new UserId(requester.getUserId()))))
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation not found.")))
                .mapNotNull(conversation -> conversationMessageRepository.conversationMessage(conversationId, messageId))
                .flatMap(message -> blockingOperation(() -> conversationMessageRepository.delete(message)));
    }

    public Mono<Page<ConversationDTO>> queryConversationArchives(Pageable pageable) {
        return Requester.currentRequester()
                .flatMap(requester -> {
                    Specification<ConversationArchive> userConversation = ((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get(ConversationArchive_.ownerUserId), new UserId(requester.getUserId())));
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(ConversationArchive_.ID).descending());

                    return blockingOperation(() -> conversationArchiveRepository.findAll(userConversation, pageRequest)
                            .map(ConversationArchiveDTO::of));
                });
    }

    public Mono<Page<MessageDTO>> queryConversationArchiveMessages(String anArchiveId, Pageable pageable) {
        ConversationArchiveId archiveId = new ConversationArchiveId(anArchiveId);

        return Requester.currentRequester().publishOn(Schedulers.boundedElastic())
                .mapNotNull(requester -> conversationArchiveRepository
                        .findByArchiveIdAndOwnerUserId(archiveId, new UserId(requester.getUserId())))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Conversation archive not found.")))
                .flatMap(archive -> conversationMessages(archive.getConversationId(), pageable));
    }

    private Mono<Page<MessageDTO>> conversationMessages(ConversationId conversationId, Pageable pageable) {
        Specification<ConversationMessage> conversationMessage = ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(ConversationMessage_.conversationId), conversationId));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Conversation_.ID).descending());

        Page<ConversationMessage> messagePage = conversationMessageRepository
                .findAll(conversationMessage, pageRequest);

        List<MessageDTO> messages = messagePage.stream()
                .sorted(Comparator.comparing(ConversationMessage::getCreatedAt))
                .map(MessageDTO::of)
                .toList();
        return Mono.just(new PageImpl<>(messages, messagePage.getPageable(), messagePage.getTotalElements()));
    }
}
