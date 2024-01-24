package com.miraclekang.chatgpt.subscription.application.equity.querystack;

import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityType;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserEquityDTO {

    @Schema(description = "User equity ID")
    private String userEquityId;

    @Schema(description = "Owner User ID")
    private String ownerUserId;
    @Schema(description = "Equity ID")
    private EquityType equityType;
    @Schema(description = "Equity ID")
    private String equityId;

    @Schema(description = "Equity name")
    private String equityName;
    @Schema(description = "Equity quantity")
    private BigDecimal quantity;
    @Schema(description = "Equity uint")
    private String unit;

    @Schema(description = "Effective time")
    private LocalDateTime effectiveTime;
    @Schema(description = "Expires time")
    private LocalDateTime expiresTime;

    @Schema(description = "Operator")
    private String operator;

    public UserEquityDTO(String userEquityId, String ownerUserId,
                         EquityType equityType, String equityId,
                         String equityName, BigDecimal quantity, String unit,
                         LocalDateTime effectiveTime, LocalDateTime expiresTime,
                         String operator) {
        this.userEquityId = userEquityId;
        this.ownerUserId = ownerUserId;
        this.equityType = equityType;
        this.equityId = equityId;
        this.equityName = equityName;
        this.quantity = quantity;
        this.unit = unit;
        this.effectiveTime = effectiveTime;
        this.expiresTime = expiresTime;
        this.operator = operator;
    }

    public static UserEquityDTO from(UserEquity userEquity) {
        return new UserEquityDTO(
                userEquity.getUserEquityId().getId(),
                userEquity.getOwnerUserId().getId(),
                userEquity.getEquityId().getType(),
                userEquity.getEquityId().getId(),
                userEquity.getEquityName(),
                userEquity.getQuantity(),
                userEquity.getUnit(),
                userEquity.getEffectiveTime(),
                userEquity.getExpiresTime(),
                userEquity.getOperator() == null ? null : userEquity.getOperator().getUserName()
        );
    }
}
