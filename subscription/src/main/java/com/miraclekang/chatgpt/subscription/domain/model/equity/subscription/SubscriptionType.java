package com.miraclekang.chatgpt.subscription.domain.model.equity.subscription;

import java.time.temporal.ChronoUnit;

public enum SubscriptionType {

    /**
     * 按天订阅
     */
    DAILY,

    /**
     * 按周订阅
     */
    WEEKLY,

    /**
     * 按月订阅
     */
    MONTHLY,

    /**
     * 按年订阅
     */
    YEARLY,
    ;

    public ChronoUnit chronoUnit() {
        return switch (this) {
            case DAILY -> ChronoUnit.DAYS;
            case WEEKLY -> ChronoUnit.WEEKS;
            case MONTHLY -> ChronoUnit.MONTHS;
            case YEARLY ->  ChronoUnit.YEARS;
        };
    }
}
