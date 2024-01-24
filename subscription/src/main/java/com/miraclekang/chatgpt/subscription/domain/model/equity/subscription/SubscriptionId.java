package com.miraclekang.chatgpt.subscription.domain.model.equity.subscription;

import com.miraclekang.chatgpt.common.model.AbstractId;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityId;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityType;
import jakarta.persistence.Embeddable;

@Embeddable
public class SubscriptionId extends AbstractId {

    public SubscriptionId() {
    }

    public SubscriptionId(String anId) {
        super(anId);
    }


    public EquityId toEquityId() {
        return new EquityId(EquityType.Subscription, this.getId());
    }
}
