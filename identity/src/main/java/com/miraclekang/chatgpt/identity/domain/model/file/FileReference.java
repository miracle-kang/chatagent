package com.miraclekang.chatgpt.identity.domain.model.file;

import com.miraclekang.chatgpt.common.model.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class FileReference extends ValueObject {

    @Getter(AccessLevel.PROTECTED)
    @Column(length = 64)
    private String fileId;

    protected FileReference() {
    }

    public FileReference(FileId fileId) {
        this.fileId = fileId.getId();
    }

    public FileReference(String fileId) {
        Validate.notBlank(fileId, "File Id must be provided.");
        this.fileId = fileId;
    }


    public FileId fileId() {
        return new FileId(fileId);
    }

    public FileBounded bind() {
        return new FileBounded(fileId());
    }

    public FileReleased release() {
        return new FileReleased(fileId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FileReference that = (FileReference) o;

        return new EqualsBuilder().append(fileId, that.fileId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(fileId).toHashCode();
    }
}
