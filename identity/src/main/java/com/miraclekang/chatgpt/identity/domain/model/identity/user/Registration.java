package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;

import java.util.Map;

public enum Registration {

    Github("github"),
    Google("google"),
    ;

    private final String value;

    Registration(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Username getUID(Map<String, String> attributes) {
        return attributes.get("uid") == null ? null : new Username(attributes.get("uid"));
    }

    public String getNickName(Map<String, String> attributes) {
        return attributes.get("name");
    }

    public Phone getPhone(Map<String, String> attributes) {
        return attributes.get("phone") == null ? null : new Phone(attributes.get("phone"));
    }

    public Email getEmail(Map<String, String> attributes) {
        return attributes.get("email") == null ? null : new Email(attributes.get("email"));
    }

    public static Registration fromValue(String value) {
        for (Registration registration : Registration.values()) {
            if (registration.value.equals(value)) {
                return registration;
            }
        }
        throw new IllegalArgumentException("Invalid registration: " + value);
    }
}
