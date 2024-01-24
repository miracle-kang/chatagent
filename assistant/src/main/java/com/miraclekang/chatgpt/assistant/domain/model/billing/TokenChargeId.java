package com.miraclekang.chatgpt.assistant.domain.model.billing;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class TokenChargeId extends AbstractId {

    public TokenChargeId() {
    }

    public TokenChargeId(String anId) {
        super(anId);
    }
}
