package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import lombok.Getter;

@Getter
public class UserRegistrationInfo extends ValueObject {

    private final Registration type;
    private final String id;

    private final String name;
    private final Email email;

    public UserRegistrationInfo(Registration type, String id, String name, Email email) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static UserRegistrationInfo of(UserRegistration registration) {
        return new UserRegistrationInfo(
                registration.getRegistrationId().getType(),
                registration.getRegistrationId().getId(),
                registration.getName(),
                registration.getEmail()
        );
    }
}
