package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.Gender;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPublicProfileDTO {

    @Schema(description = "Nickname")
    private String nickname;
    @Schema(description = "Gender")
    private Gender gender;
    @Schema(description = "Avatar URL")
    private String avatarUrl;
    @Schema(description = "Description")
    private String description;

    public UserPublicProfileDTO(String nickname, Gender gender, String avatarUrl, String description) {
        this.nickname = nickname;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.description = description;
    }


    public static UserPublicProfileDTO from(UserProfile profile) {
        return new UserPublicProfileDTO(
                profile.getNickname(),
                profile.getGender(),
                profile.getAvatarUrl(),
                profile.getDescription()
        );
    }
}
