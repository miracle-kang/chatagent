package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class ConversationArchiveId extends AbstractId {

    public ConversationArchiveId() {
    }

    public ConversationArchiveId(String anId) {
        super(anId);
    }
}
