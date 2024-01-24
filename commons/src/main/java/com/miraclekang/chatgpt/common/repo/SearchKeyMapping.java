package com.miraclekang.chatgpt.common.repo;

@FunctionalInterface
public interface SearchKeyMapping {

    String apply(String key);

    static SearchKeyMapping defaultMapping() {
        return key -> key;
    }
}
