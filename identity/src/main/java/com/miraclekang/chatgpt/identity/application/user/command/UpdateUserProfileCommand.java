package com.miraclekang.chatgpt.identity.application.user.command;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class UpdateUserProfileCommand {

    @Schema(description = "Nickname")
    private String nickname;
    @Schema(description = "Gender")
    private Gender gender;
    @Schema(description = "Avatar URL")
    private String avatarUrl;
    @Schema(description = "Description")
    private String description;

    @Schema(description = "Custom settings")
    private Map<String, String> settings;
}
