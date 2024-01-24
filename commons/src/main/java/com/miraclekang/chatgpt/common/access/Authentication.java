package com.miraclekang.chatgpt.common.access;

public interface Authentication extends org.springframework.security.core.Authentication {

    String getUserType();

    String getUserId();

    String getUsername();
}
