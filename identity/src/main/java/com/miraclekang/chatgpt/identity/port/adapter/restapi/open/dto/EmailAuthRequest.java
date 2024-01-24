package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmailAuthRequest extends EmailRequest {

    @Schema(description = "Auth Code")
    @NotBlank(message = "Email auth code must not be null")
    private String authCode;
}
