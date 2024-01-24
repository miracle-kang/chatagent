package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public final class MessageConfig extends ValueObject {

    private final ChatModel model;           // default gpt-3.5-turbo
    private final Double temperature;     // default 0.7
    private final Double topP;            // default 1
    private final Integer maxTokens;      // default 2000

    // System configure
    private final String systemMessage;  // default null

    private final Integer choices;        // default 1
    private final List<String> stop;
    private final Double presencePenalty;     // default 0
    private final Double frequencyPenalty;    // default 0
    private final String endUser;             // default null
    private final Map<String, Integer> logitBias;

    public MessageConfig(ChatModel model, Double temperature, Double topP, Integer maxTokens,
                         String systemMessage, Integer choices, List<String> stop, Double presencePenalty,
                         Double frequencyPenalty, String endUser, Map<String, Integer> logitBias) {
        this.model = Objects.requireNonNullElse(model, ChatModel.GPT3_5);
        this.temperature = Objects.requireNonNullElse(temperature, 0.7);
        this.topP = Objects.requireNonNullElse(topP, 1.0);
        this.maxTokens = Objects.requireNonNullElse(maxTokens, 2000);

        this.systemMessage = systemMessage;
        this.choices = Objects.requireNonNullElse(choices, 1);
        this.stop = stop;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
        this.endUser = endUser;
        this.logitBias = logitBias;
    }
}
