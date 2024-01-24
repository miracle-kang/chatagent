package com.miraclekang.chatgpt.identity.domain.model.file;

import com.miraclekang.chatgpt.common.model.AbstractId;
import jakarta.persistence.Embeddable;

@Embeddable
public class FileId extends AbstractId {

    public FileId() {
    }

    public FileId(String anId) {
        super(anId);
    }
}
