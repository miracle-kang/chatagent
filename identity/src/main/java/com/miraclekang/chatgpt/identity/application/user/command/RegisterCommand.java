package com.miraclekang.chatgpt.identity.application.user.command;

import com.miraclekang.chatgpt.identity.application.user.querystack.UserProfileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterCommand {

    @Schema(description = "Phone area", defaultValue = "86")
    private String phoneArea;
    @Schema(description = "Phone number")
    private String phoneNumber;
    @Schema(description = "Phone auth code, required when phone number is provided")
    private String phoneAuthCode;

    @Schema(description = "Email address")
    private String emailAddress;
    @Schema(description = "Email auth code, required when email address is provided")
    private String emailAuthCode;

    @Schema(description = "Username")
    @NotBlank(message = "Username must not be null")
    private String username;
    @Schema(description = "Password")
    private String password;

    @Schema(description = "Invite code")
    private String inviteCode;

    @Schema(description = "User profile")
    private UserProfileDTO profile;
}
