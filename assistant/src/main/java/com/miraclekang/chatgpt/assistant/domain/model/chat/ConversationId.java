package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class ConversationId extends AbstractId {

    public ConversationId() {
    }

    public ConversationId(String anId) {
        super(anId);
    }
}
