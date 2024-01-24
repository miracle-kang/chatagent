package com.miraclekang.chatgpt.identity.domain.model.equity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
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

    public boolean available() {
        return status() == UserEquityStatus.AVAILABLE;
    }

    public UserEquityStatus status() {
        if (LocalDateTime.now().isBefore(effectiveTime)) {
            return UserEquityStatus.INEFFECTIVE;
        }
        if (LocalDateTime.now().isAfter(expiresTime)) {
            return UserEquityStatus.EXPIRES;
        }
        return UserEquityStatus.AVAILABLE;
    }
}
