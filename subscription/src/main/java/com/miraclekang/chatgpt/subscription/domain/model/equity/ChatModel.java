package com.miraclekang.chatgpt.subscription.domain.model.equity;

public enum ChatModel {

    GPT3_5("gpt-3.5-turbo", 4096),

    GPT4("gpt-4", 8192),
    ;

    private final String id;
    private final int maxTokens;

    ChatModel(String id, int maxTokens) {
        this.id = id;
        this.maxTokens = maxTokens;
    }

    public String getId() {
        return id;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public static ChatModel ofId(String id) {
        for (ChatModel model : values()) {
            if (model.getId().equals(id)) {
                return model;
            }
        }
        return null;
    }
}
