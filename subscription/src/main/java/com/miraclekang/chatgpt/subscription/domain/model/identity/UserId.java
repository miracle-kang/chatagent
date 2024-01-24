package com.miraclekang.chatgpt.subscription.domain.model.identity;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserId extends AbstractId {

    public UserId() {
    }

    public UserId(String anId) {
        super(anId);
    }
}
