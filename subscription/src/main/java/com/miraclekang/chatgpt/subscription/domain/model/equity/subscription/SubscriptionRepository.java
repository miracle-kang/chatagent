package com.miraclekang.chatgpt.subscription.domain.model.equity.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
        JpaSpecificationExecutor<Subscription> {

    Subscription findBySubscriptionId(SubscriptionId equityId);
}
