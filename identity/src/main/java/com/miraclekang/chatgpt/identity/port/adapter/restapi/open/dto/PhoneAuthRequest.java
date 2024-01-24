package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhoneAuthRequest extends PhoneRequest {

    @Schema(description = "Auth Code", defaultValue = "666666")
    @NotBlank(message = "Email auth code must not be null")
    private String authCode;
}
