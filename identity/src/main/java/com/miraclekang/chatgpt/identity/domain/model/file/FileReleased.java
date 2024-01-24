package com.miraclekang.chatgpt.identity.domain.model.file;

import com.miraclekang.chatgpt.common.model.DomainEvent;
import lombok.Getter;

@Getter
public class FileReleased extends DomainEvent {

    private final FileId fileId;

    public FileReleased(FileId fileId) {
        this.fileId = fileId;
    }
}
