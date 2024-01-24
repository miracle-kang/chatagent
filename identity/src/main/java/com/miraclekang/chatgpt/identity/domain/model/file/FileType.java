package com.miraclekang.chatgpt.identity.domain.model.file;

public enum FileType {

    AVATAR("avatars", 1024 * 1024),     // Limit 1MB

    IMAGE("images", 10 * 1024 * 1024),  // Limit 10MB
    ;

    private final String path;
    private final long limitSize;

    FileType(String path, long limitSize) {
        this.path = path;
        this.limitSize = limitSize;
    }

    public String getPath() {
        return path;
    }

    public long getLimitSize() {
        return limitSize;
    }
}
