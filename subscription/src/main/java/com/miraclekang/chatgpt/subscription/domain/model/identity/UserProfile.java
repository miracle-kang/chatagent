package com.miraclekang.chatgpt.subscription.domain.model.identity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class UserProfile extends ValueObject {

    // @Comment("User Theme")
    private final String nickname;
    // @Comment("User Gender")
    private final String gender;

    // @Comment("User avatar url")
    private final String avatarUrl;
    // @Comment("User Description")
    private final String description;

    // @Comment("Custom Settings")
    private final Map<String, String> settings = new LinkedHashMap<>();

    public UserProfile(String nickname, String gender, String avatarUrl,
                       String description, Map<String, String> settings) {
        this.nickname = nickname;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.description = description;
        if (settings != null) {
            this.settings.putAll(settings);
        }
    }
}
