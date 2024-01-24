package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class AdministratorId extends AbstractId {

    public AdministratorId() {
    }

    public AdministratorId(String anId) {
        super(anId);
    }
}
