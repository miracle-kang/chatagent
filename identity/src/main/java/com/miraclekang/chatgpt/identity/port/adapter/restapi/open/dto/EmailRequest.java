package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {

    @Schema(description = "Email Address")
    @NotBlank(message = "Email address must not be null")
    private String emailAddress;
}
