package com.miraclekang.chatgpt.identity.application.user.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChangeUsernameCommand {

    @Schema(description = "New username")
    private String username;
}
