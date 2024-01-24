package com.miraclekang.chatgpt.common.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity extends IdentifiedDomainObject {

    @CreatedDate
    // @Comment("Created time")
    private LocalDateTime createdAt;

    @LastModifiedDate
    // @Comment("Last modified time")
    private LocalDateTime lastModifiedAt;

    @PreRemove
    protected final void preRemove() {
        if (this instanceof EntityCleaner) {
            ((EntityCleaner) this).clean();
        }

        this.onRemove();
    }

    /**
     * 实现类在需要的时候覆盖该方法
     */
    protected void onRemove() {
        // ignore
    }

    @PrePersist
    protected final void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
        this.setLastModifiedAt(LocalDateTime.now());
    }

    @PreUpdate
    protected final void onUpdate() {
        this.setLastModifiedAt(LocalDateTime.now());
    }

    protected LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    protected LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    private void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}
