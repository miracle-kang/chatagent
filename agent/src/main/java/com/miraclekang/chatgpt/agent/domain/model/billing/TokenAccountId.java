package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class TokenAccountId extends AbstractId {

    public TokenAccountId() {
    }

    public TokenAccountId(String anId) {
        super(anId);
    }
}
