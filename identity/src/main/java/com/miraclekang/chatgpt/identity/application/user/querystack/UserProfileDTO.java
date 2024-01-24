package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.Gender;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class UserProfileDTO {

    @Schema(description = "Nickname")
    private String nickname;
    @Schema(description = "Gender")
    private Gender gender;
    @Schema(description = "Avatar URL")
    private String avatarUrl;
    @Schema(description = "Description")
    private String description;

    @Schema(description = "Setting map")
    private Map<String, String> settings;

    public UserProfileDTO() {
    }

    public UserProfileDTO(String nickname, Gender gender, String avatarUrl, String description,
                          Map<String, String> settings) {
        this.nickname = nickname;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.settings = settings;
    }

    public UserProfile toProfile() {
        return new UserProfile(
                nickname,
                gender,
                avatarUrl,
                description,
                settings
        );
    }

    public static UserProfileDTO from(UserProfile profile) {
        if (profile == null)
            return new UserProfileDTO();
        return new UserProfileDTO(
                profile.getNickname(),
                profile.getGender(),
                profile.getAvatarUrl(),
                profile.getDescription(),
                profile.getSettings());
    }
}
