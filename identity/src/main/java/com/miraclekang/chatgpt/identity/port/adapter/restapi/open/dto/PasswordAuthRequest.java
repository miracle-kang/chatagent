package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordAuthRequest {

    @Schema(description = "Identity, email or phone number, or username")
    @NotBlank(message = "Identity is required")
    private String identity;

    @Schema(description = "Password")
    @NotBlank(message = "Password is required")
    private String password;
}
