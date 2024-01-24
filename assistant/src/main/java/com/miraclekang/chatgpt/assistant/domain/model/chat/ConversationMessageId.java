package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class ConversationMessageId extends AbstractId {

    public ConversationMessageId() {
    }

    public ConversationMessageId(String anId) {
        super(anId);
    }
}
