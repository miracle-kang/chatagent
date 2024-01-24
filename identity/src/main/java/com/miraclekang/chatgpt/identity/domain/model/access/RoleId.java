package com.miraclekang.chatgpt.identity.domain.model.access;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class RoleId extends AbstractId {

    public RoleId() {
    }

    public RoleId(String anId) {
        super(anId);
    }
}
