package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserInfo;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoDTO {

    @Schema(description = "User ID")
    private String userId;
    @Schema(description = "Username")
    private String username;
    @Schema(description = "Email")
    private String email;
    @Schema(description = "Phone")
    private String phone;
    @Schema(description = "Role")
    private UserType role;

    @Schema(description = "Profile")
    private UserProfileDTO profile;

    public UserInfoDTO(String userId, String username, String email, String phone, UserType role, UserProfileDTO profile) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profile = profile;
    }

    public static UserInfoDTO from(UserInfo userInfo) {
        return new UserInfoDTO(
                userInfo.getUserId().getId(),
                userInfo.getUsername().getUsername(),
                userInfo.getEmail() == null ? null : userInfo.getEmail().getAddress(),
                userInfo.getPhone() == null ? null : userInfo.getPhone().fullProtectedNumber(),
                userInfo.getRole(),
                UserProfileDTO.from(userInfo.getProfile())
        );
    }
}
