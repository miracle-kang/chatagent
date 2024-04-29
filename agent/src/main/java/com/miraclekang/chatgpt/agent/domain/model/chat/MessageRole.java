package com.miraclekang.chatgpt.agent.domain.model.chat;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.apache.commons.lang3.StringUtils;

public enum MessageRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String value;

    MessageRole(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static MessageRole of(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (MessageRole messageRole : MessageRole.values()) {
            if (messageRole.value.equals(value)) {
                return messageRole;
            }
        }
        return null;
    }

    public static MessageRole of(ChatMessageRole chatMessageRole) {
        if (chatMessageRole == null) {
            return null;
        }
        return switch (chatMessageRole) {
            case USER -> USER;
            case ASSISTANT -> ASSISTANT;
            case SYSTEM -> SYSTEM;
            default -> null;
        };
    }
}
