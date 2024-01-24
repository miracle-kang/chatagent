package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class CustomerId extends AbstractId {

    public CustomerId() {
    }

    public CustomerId(String anId) {
        super(anId);
    }
}
