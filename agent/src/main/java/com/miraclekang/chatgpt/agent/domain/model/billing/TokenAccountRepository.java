package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.agent.domain.model.identity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TokenAccountRepository extends JpaRepository<TokenAccount, Long>,
        JpaSpecificationExecutor<TokenAccount> {

    TokenAccount findByAccountId(TokenAccountId accountId);

    List<TokenAccount> findByOwnerUserId(UserId userId);
    TokenAccount findByOwnerUserIdAndModel(UserId userId, ChatModel model);
}
