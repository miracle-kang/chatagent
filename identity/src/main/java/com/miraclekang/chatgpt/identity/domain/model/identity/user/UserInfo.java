package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import lombok.Getter;

@Getter
public class UserInfo extends ValueObject {

    private final UserId userId;
    private final Username username;
    private final Email email;
    private final Phone phone;
    private final UserType role;

    private final UserProfile profile;

    public UserInfo(UserId userId, Username username, Email email, Phone phone, UserType role, UserProfile profile) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profile = profile;
    }

}
