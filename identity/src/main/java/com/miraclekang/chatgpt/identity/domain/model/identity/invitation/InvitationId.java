package com.miraclekang.chatgpt.identity.domain.model.identity.invitation;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class InvitationId extends AbstractId {

    public InvitationId() {
    }

    public InvitationId(String anId) {
        super(anId);
    }
}
