package com.miraclekang.chatgpt.assistant.port.adapter.service;

import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatService;
import com.miraclekang.chatgpt.assistant.domain.model.chat.Message;
import com.miraclekang.chatgpt.assistant.domain.model.chat.MessageConfig;
import com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.openai.OpenAIClient;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OpenAIServiceImpl implements ChatService {

    private final OpenAIClient openaiClient;

    public OpenAIServiceImpl(@Value("${third-party.openai.token}") String openaiToken,
                             @Value("${third-party.openai.proxy:none}") String openaiProxy) {

        this.openaiClient = new OpenAIClient(openaiToken, openaiProxy);
    }

    @Override
    public Message sendMessages(List<Message> messages, MessageConfig config) {
        return prvSendMessages(messages, config, false)
                .blockFirst();
    }

    @Override
    public Flux<Message> fluxSendMessages(List<Message> messages, MessageConfig config) {
        return prvSendMessages(messages, config, true);
    }

    private Flux<Message> prvSendMessages(List<Message> messages, MessageConfig config, boolean stream) {
        return openaiClient.chatCompletion(ChatCompletionRequest.builder()
                        .model(config.getModel().getId())
                        .temperature(config.getTemperature())
                        .topP(config.getTopP())
                        .maxTokens(config.getMaxTokens())
                        .messages(messages.stream().map(Message::toChatMessage).toList())
                        .n(config.getChoices())
                        .stream(stream)
                        .stop(config.getStop() == null || config.getStop().isEmpty()
                                ? null : config.getStop())
                        .logitBias(config.getLogitBias() == null || config.getLogitBias().isEmpty()
                                ? null : config.getLogitBias())
                        .user(config.getEndUser())
                        .build())
                .filter(chatMessage -> !stream || Objects.nonNull(chatMessage)
                        && (chatMessage.getRole() != null || chatMessage.getContent() != null))
                .mapNotNull(Message::of);
    }
}
