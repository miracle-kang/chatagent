package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import com.miraclekang.chatgpt.identity.domain.model.identity.VerificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhoneVerificationRequest extends PhoneRequest {

    @Schema(description = "Verification type", defaultValue = "Login")
    private VerificationType type;
}
