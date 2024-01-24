package com.miraclekang.chatgpt.identity.domain.model.file;

import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.model.AggregateRoot;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.OperatorUser;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.miraclekang.chatgpt.identity.domain.model.file.FileStatus.CREATED;
import static com.miraclekang.chatgpt.identity.domain.model.file.FileStatus.DELETED;

@Entity
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "IDX_file_id", columnList = "fileId", unique = true),
})
public class File extends AggregateRoot {

    // @Comment("File ID")
    @AttributeOverride(name = "id", column = @Column(name = "fileId", length = 64, nullable = false))
    private FileId fileId;

    // @Comment("File Type")
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private FileType type;

    // @Comment("File Path/Bucket")
    private String path;

    // @Comment("File Name")
    private String name;

    // @Comment("File size")
    private Long size;

    // @Comment("File MD5")
    private String md5;

    // @Comment("File MIME Type")
    private String mimeType;

    // @Comment("File Owner")
    @AttributeOverride(name = "id", column = @Column(name = "ownerId", length = 64, nullable = false))
    private UserId owner;

    // @Comment("File Scope")
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private FileScope scope;

    @Getter(AccessLevel.PROTECTED)
    // @Comment("File Status")
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private FileStatus status;

    // @Comment("File Reference Count")
    private Integer referenceCount = 0;

    @Embedded
    @AttributeOverrides( value = {
            @AttributeOverride(name = "userId", column = @Column(name = "operatorUserId", length = 64)),
            @AttributeOverride(name = "userName", column = @Column(name = "operatorUserName", length = 128)),
            @AttributeOverride(name = "userIp", column = @Column(name = "operatorUserIp", length = 64))
    })
    private OperatorUser operator;

    public File(FileId fileId, FileType type, String name, Long size, String md5, String mimeType,
                FileScope scope, FileStatus status, Requester requester) {
        this.fileId = fileId;
        this.type = type;
        this.path = type.getPath();
        this.name = name;
        this.size = size;
        this.md5 = md5;
        this.mimeType = mimeType;
        this.owner = new UserId(requester.getUserId());
        this.scope = scope;
        this.status = status;

        this.operator = OperatorUser.requester(requester);
    }

    public boolean canAccess(Requester requester) {
        return scope == FileScope.PUBLIC || requester.isAdmin()
                || (requester.isAuthenticated() && Objects.equals(owner, requester.getUserId()));
    }

    public void bind() {
        this.referenceCount++;

        this.status = FileStatus.BOUNDED;
    }

    public void release() {
        this.referenceCount--;
        if (this.referenceCount <= 0) {
            this.status = FileStatus.RELEASED;
        }
    }

    public String previewUrl(FileStorageService storageService, Requester requester) {
        if (this.status == CREATED || this.status == DELETED || !storageService.reachable(this)) {
            return null;
        }
        if (!canAccess(requester)) {
            return null;
        }
        return storageService.previewUrl(this.getFileId(), requester).getUrl();
    }

    public Mono<Void> delete(FileStorageService storageService) {
        return storageService.delete(this)
                .then(Mono.fromRunnable(() -> this.status = DELETED));
    }

    public FileStatus status(FileStorageService storageService) {
        if (this.status == CREATED || this.status == DELETED) {
            return status;
        }
        if (!storageService.reachable(this)) {
            return FileStatus.UNREACHABLE;
        }
        return status;
    }
}
