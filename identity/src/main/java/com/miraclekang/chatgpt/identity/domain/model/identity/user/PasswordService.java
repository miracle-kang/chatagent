package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final String passwordSecret = "M3PYidVcx1142PHXupMx";

    public String randPassword() {
        return RandomStringUtils.random(16, true, true);
    }

    public String encryptPassword(String plainPassword, String salt) {
        Validate.notBlank(plainPassword, "Password must be provided");
        return DigestUtils.sha256Hex(DigestUtils.sha256Hex(plainPassword + salt) + passwordSecret);
    }
}
