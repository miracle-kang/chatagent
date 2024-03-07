package com.miraclekang.chatgpt.assistant.domain.model.identity;

import lombok.Getter;

@Getter
public class UserInfo {

    private final UserId userId;
    private final String username;
    private final String email;
    private final String phone;
    private final UserType role;

    private final UserProfile profile;

    public UserInfo(UserId userId, String username, String email, String phone, UserType role,
                    UserProfile profile) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profile = profile;
    }
}
