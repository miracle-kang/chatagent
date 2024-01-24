package com.miraclekang.chatgpt.subscription.application.equity.querystack;

import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityLimitation;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityType;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquityInfo;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class UserEquityInfoDTO {

    @Schema(description = "User equity ID")
    private String userEquityId;
    @Schema(description = "User equity ID")
    private EquityType equityType;
    @Schema(description = "User equity ID")
    private String equityId;

    @Schema(description = "Equity name")
    private String equityName;
    @Schema(description = "Equity quantity")
    private BigDecimal quantity;
    @Schema(description = "Equity uint")
    private String unit;

    @Schema(description = "Effective time")
    private ZonedDateTime effectiveTime;
    @Schema(description = "Expires time")
    private ZonedDateTime expiresTime;
    @Schema(description = "Equity status")
    private UserEquityStatus status;

    @Schema(description = "Equity limitation")
    private EquityLimitation limitation;

    public UserEquityInfoDTO(String userEquityId, EquityType equityType, String equityId,
                             String equityName, BigDecimal quantity, String unit,
                             ZonedDateTime effectiveTime, ZonedDateTime expiresTime,
                             UserEquityStatus status, EquityLimitation limitation) {
        this.userEquityId = userEquityId;
        this.equityType = equityType;
        this.equityId = equityId;
        this.equityName = equityName;
        this.quantity = quantity;
        this.unit = unit;
        this.effectiveTime = effectiveTime;
        this.expiresTime = expiresTime;
        this.status = status;
        this.limitation = limitation;
    }

    public static UserEquityInfoDTO from(UserEquityInfo equityInfo) {
        return new UserEquityInfoDTO(
                equityInfo.getUserEquityId().getId(),
                equityInfo.getEquityId().getType(),
                equityInfo.getEquityId().getId(),
                equityInfo.getEquityName(),
                equityInfo.getQuantity(),
                equityInfo.getUnit(),
                equityInfo.getEffectiveTime() == null ? null : equityInfo.getEffectiveTime().atZone(ZoneId.systemDefault()),
                equityInfo.getExpiresTime() == null ? null : equityInfo.getExpiresTime().atZone(ZoneId.systemDefault()),
                equityInfo.getStatus(),
                equityInfo.getLimitation()
        );
    }
}
