package com.miraclekang.chatgpt.subscription.domain.model.equity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface Equity {

    EquityId equityId();

    default EquityType type() {
        return equityId().getType();
    }

    String name();

    String unit();

    default LocalDateTime effectiveTime(LocalDateTime baseTime) {
        return baseTime;
    }

    LocalDateTime expiresTime(LocalDateTime effectiveTime, BigDecimal quantity);

    EquityLimitation limitation();
}
