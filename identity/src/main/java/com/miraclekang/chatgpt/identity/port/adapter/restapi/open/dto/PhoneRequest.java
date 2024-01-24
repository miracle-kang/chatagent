package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneRequest {

    @Schema(description = "Phone area", defaultValue = "86")
    @NotBlank(message = "Phone area must not be null")
    private String phoneArea;

    @Schema(description = "Phone number")
    @NotBlank(message = "Phone number must not be null")
    private String phoneNumber;
}
