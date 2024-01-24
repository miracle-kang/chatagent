package com.miraclekang.chatgpt.identity.domain.model.file;

import com.miraclekang.chatgpt.common.reactive.Requester;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileStorageService {

    Mono<File> upload(Requester requester, FileType type, FileScope scope, FilePart filePart);

    FilePreviewUrl previewUrl(FileId fileId, Requester requester);

    Mono<Void> preview(File file, Requester requester);

    boolean reachable(File file);

    Mono<Void> delete(File file);
}
