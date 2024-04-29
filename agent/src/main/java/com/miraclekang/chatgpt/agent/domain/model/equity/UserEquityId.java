package com.miraclekang.chatgpt.agent.domain.model.equity;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserEquityId extends AbstractId {

    public UserEquityId() {
    }

    public UserEquityId(String anId) {
        super(anId);
    }
}
