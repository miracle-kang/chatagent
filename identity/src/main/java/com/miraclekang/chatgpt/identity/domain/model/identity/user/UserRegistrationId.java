package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
public class UserRegistrationId {

    // @Comment("Registration type")
    @Enumerated(EnumType.STRING)
    @Column(name = "registrationType", length = 16)
    private Registration type;

    // @Comment("Registration ID")
    @Column(name = "registrationId", length = 64)
    private String id;

    protected UserRegistrationId() {
    }

    public UserRegistrationId(Registration type, String id) {
        this.type = type;
        this.id = id;
    }

    public static UserRegistrationId of(Registration type, String id) {
        return new UserRegistrationId(type, id);
    }
}
