package com.miraclekang.chatgpt.assistant.domain.model.billing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenChargeRepository extends JpaRepository<TokenCharge, Long> {
}
