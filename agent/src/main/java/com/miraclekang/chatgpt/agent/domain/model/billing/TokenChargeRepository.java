package com.miraclekang.chatgpt.agent.domain.model.billing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenChargeRepository extends JpaRepository<TokenCharge, Long> {
}
