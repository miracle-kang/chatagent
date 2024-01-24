package com.miraclekang.chatgpt.subscription.port.adapter.restapi.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenewalUserEquityRequest {

    @Schema(description = "Renewal quantity")
    @NotNull(message = "Renewal quantity must not be null")
    @Min(value = 0, message = "Renewal quantity must be greater than or equal to 0")
    private Long renewalQty;
}
