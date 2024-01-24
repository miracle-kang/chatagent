package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Schema(description = "Old Password")
    @NotBlank(message = "Old password must not be null")
    private String password;

    @Schema(description = "New Password")
    @NotBlank(message = "New password must not be null")
    private String newPassword;
}
