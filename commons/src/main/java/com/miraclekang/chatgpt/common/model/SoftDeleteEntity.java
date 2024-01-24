package com.miraclekang.chatgpt.common.model;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class SoftDeleteEntity extends BaseEntity {

    private boolean deleted = Boolean.FALSE;

    protected boolean isDeleted() {
        return deleted;
    }

    protected void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
