package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.common.repo.Converters;
import com.miraclekang.chatgpt.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class UserProfile extends BaseEntity {

    // @Comment("User Theme")
    private String nickname;
    // @Comment("User Gender")
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private Gender gender;

    // @Comment("User avatar url")
    @Column(length = 512)
    private String avatarUrl;
    // @Comment("User Description")
    @Column(length = 512)
    private String description;

    // @Comment("Custom Settings")
    @Column(columnDefinition = "TEXT")
    @Convert(converter = Converters.StringMapConverter.class)
    private final Map<String, String> settings = new LinkedHashMap<>();

    public UserProfile(String nickname, Gender gender, String avatarUrl, String description,
                       Map<String, String> settings) {
        this.nickname = nickname;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.description = description;
        if (settings != null) {
            this.settings.putAll(settings);
        }
    }

    public UserProfile update(String nickname, Gender gender, String avatarUrl, String description,
                              Map<String, String> settings) {
        this.nickname = nickname;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.description = description;

        if (settings != null) {
            this.settings.clear();
            this.settings.putAll(settings);
        }

        return this;
    }

    public UserProfile update(UserProfile profile) {
        this.nickname = profile.getNickname();
        this.gender = profile.getGender();
        this.avatarUrl = profile.getAvatarUrl();
        this.description = profile.getDescription();

        this.settings.clear();
        this.settings.putAll(profile.getSettings());

        return this;
    }

    public static UserProfile defaultProfile(String name) {
        return new UserProfile(name, null, null, null,
                Map.of(
                        "theme", "light",
                        "language", "zh"
                ));
    }
}
