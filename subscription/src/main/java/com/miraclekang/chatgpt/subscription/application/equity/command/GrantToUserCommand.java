package com.miraclekang.chatgpt.subscription.application.equity.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GrantToUserCommand {

    @Schema(description = "User id to granted")
    private String userId;

    @Schema(description = "Quantity to granted")
    private BigDecimal quantity;

}
