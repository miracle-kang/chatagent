package com.miraclekang.chatgpt.identity.domain.model.file;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

@Getter
public class FilePreviewUrl extends ValueObject {

    private final String fileId;
    private final String url;

    public FilePreviewUrl(String fileId, String url) {
        this.fileId = fileId;
        this.url = url;
    }
}
