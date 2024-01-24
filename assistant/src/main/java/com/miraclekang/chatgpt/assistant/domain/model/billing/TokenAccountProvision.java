package com.miraclekang.chatgpt.assistant.domain.model.billing;

import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import org.springframework.stereotype.Component;

@Component
public class TokenAccountProvision {

    private final TokenAccountRepository tokenAccountRepository;

    public TokenAccountProvision(TokenAccountRepository tokenAccountRepository) {
        this.tokenAccountRepository = tokenAccountRepository;
    }

    public TokenAccount provision(UserId userId, ChatModel model) {
        TokenAccount userAccount = tokenAccountRepository.findByOwnerUserIdAndModel(userId, model);
        if (userAccount != null) {
            return userAccount;
        }
        synchronized (this) {
            userAccount = tokenAccountRepository.findByOwnerUserIdAndModel(userId, model);
            if (userAccount != null) {
                return userAccount;
            }
            userAccount = new TokenAccount(
                    new TokenAccountId(IdentityGenerator.nextIdentity()),
                    userId,
                    model
            );
            return tokenAccountRepository.save(userAccount);
        }
    }
}
