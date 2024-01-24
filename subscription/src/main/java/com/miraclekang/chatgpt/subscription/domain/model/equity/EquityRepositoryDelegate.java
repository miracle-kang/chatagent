package com.miraclekang.chatgpt.subscription.domain.model.equity;

import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionId;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class EquityRepositoryDelegate {

    private final SubscriptionRepository subscriptionRepository;

    public EquityRepositoryDelegate(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Equity getEquity(EquityId equityId) {
        if (Objects.requireNonNull(equityId.getType()) == EquityType.Subscription) {
            return subscriptionRepository.findBySubscriptionId(new SubscriptionId(equityId.getId()));
        }
        throw new IllegalArgumentException("Unknown equity id " + equityId);
    }
}
