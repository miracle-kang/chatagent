package com.miraclekang.chatgpt.identity.port.adapter.service.authcode;

public interface AuthCodeValidator {

    String generateAuthCode(String identity, int expiresMinutes);

    boolean validateAuthCode(String identity, String authCode);
}
