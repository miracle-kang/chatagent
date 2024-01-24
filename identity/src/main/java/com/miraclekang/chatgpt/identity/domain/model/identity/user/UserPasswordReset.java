package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.common.model.DomainEvent;
import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import lombok.Getter;

@Getter
public class UserPasswordReset extends DomainEvent {

    private final UserId userId;
    private final Username username;
    private final Email email;
    private final Phone phone;
    private final String randPassword;

    public UserPasswordReset(UserId userId, Username username, Email email, Phone phone, String randPassword) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.randPassword = randPassword;
    }
}
