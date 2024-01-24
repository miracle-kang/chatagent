package com.miraclekang.chatgpt.subscription.domain.model.equity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class UserEquityInfo extends ValueObject {

    private final UserEquityId userEquityId;
    private final EquityId equityId;
    private final String equityName;
    private final BigDecimal quantity;
    private final String unit;

    private final LocalDateTime effectiveTime;
    private final LocalDateTime expiresTime;
    private final UserEquityStatus status;

    private final EquityLimitation limitation;

    public UserEquityInfo(UserEquityId userEquityId, EquityId equityId, String equityName,
                          BigDecimal quantity, String unit,
                          LocalDateTime effectiveTime,
                          LocalDateTime expiresTime,
                          UserEquityStatus status, EquityLimitation limitation) {
        this.userEquityId = userEquityId;
        this.equityId = equityId;
        this.equityName = equityName;
        this.quantity = quantity;
        this.unit = unit;
        this.effectiveTime = effectiveTime;
        this.expiresTime = expiresTime;
        this.status = status;
        this.limitation = limitation;
    }
}
