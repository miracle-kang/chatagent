package com.miraclekang.chatgpt.identity.port.adapter.service.storage;

import com.miraclekang.chatgpt.common.exception.ApplicationException;
import com.miraclekang.chatgpt.identity.domain.EnumExceptionCode;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.domain.model.file.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

public class DirectoryFileStorageService implements FileStorageService {

    private final static long MAX_FILE_SIZE = 1024 * 1024 * 1024; // 1GB

    private final Path storagePath;

    public DirectoryFileStorageService(String storagePath) {
        this.storagePath = Path.of(storagePath);

        if (!Files.isDirectory(this.storagePath)) {
            try {
                Files.createDirectories(this.storagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Mono<File> upload(Requester requester, FileType type, FileScope scope, FilePart filePart) {
        long fileSize = filePart.headers().getContentLength() > 0 ? filePart.headers().getContentLength()
                : requester.getExchange().getRequest().getHeaders().getContentLength();
        if (fileSize > type.getLimitSize() || fileSize > MAX_FILE_SIZE) {
            return Mono.error(new ApplicationException(EnumExceptionCode.BadRequest, "File is too large"));
        }

        FileId fileId = new FileId(IdentityGenerator.nextIdentity());
        Path subPath = storagePath.resolve(type.getPath());

        return Mono.just(subPath)
                .filter(Files::isDirectory)
                .switchIfEmpty(blockingOperation(() -> Files.createDirectories(subPath)))
                .flatMap(path -> blockingOperation(() -> Files.createFile(path.resolve(fileId.toString()))))
                .flatMap(dest -> {
                    MessageDigest messageDigest = DigestUtils.getMd5Digest();
                    return Mono.<String>create(sink -> {
                        try {
                            AsynchronousFileChannel channel = AsynchronousFileChannel.open(dest, StandardOpenOption.WRITE);
                            sink.onDispose(() -> closeChannel(channel));
                            DataBufferUtils.write(filePart.content(), channel).subscribe(
                                    buffer -> {
                                        byte[] bytes = new byte[buffer.readableByteCount()];
                                        buffer.read(bytes);
                                        messageDigest.update(bytes);
                                        DataBufferUtils.release(buffer);
                                    },
                                    sink::error,
                                    () -> sink.success(Hex.encodeHexString(messageDigest.digest())));
                        } catch (IOException ex) {
                            sink.error(ex);
                        }
                    }).subscribeOn(Schedulers.boundedElastic());
                }).map(md5 -> {
                    MediaType contentType = filePart.headers().getContentType() != null ? filePart.headers().getContentType()
                            : MediaTypeFactory.getMediaType(filePart.filename()).orElse(null);
                    return new File(
                            fileId,
                            type,
                            filePart.filename(),
                            fileSize,
                            md5,
                            contentType != null ? contentType.toString() : null,
                            scope,
                            FileStatus.UPLOADED,
                            requester
                    );
                });
    }

    private static void closeChannel(@Nullable Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public FilePreviewUrl previewUrl(FileId fileId, Requester requester) {
        if (fileId == null) {
            return new FilePreviewUrl(null, null);
        }
        URI requestURI = requester.getExchange().getRequest().getURI();
        InetSocketAddress requestHost = requester.getExchange().getRequest().getHeaders().getHost();
        int port;
        if (requestHost == null) {
            port = requestURI.getPort();
        } else {
            port = requestHost.getPort();
        }
        return new FilePreviewUrl(fileId.getId(),
                requestURI.getScheme() + "://" + requestURI.getHost() + (port > 0 ? ":" + port : "") +
                        "/api/open/common/files/" + fileId.getId() + "/preview");
    }

    @Override
    public Mono<Void> preview(File file, Requester requester) {
        if (!reachable(file)) {
            return Mono.error(new ApplicationException(EnumExceptionCode.NotFound, "File is un-reachable"));
        }
        return Mono.just(Path.of(file.getPath()))
                .map(path -> storagePath.resolve(path).resolve(file.getFileId().getId()))
                .flatMap(path -> {
                    if (!Files.exists(path) || !Files.isReadable(path)) {
                        return Mono.error(new ApplicationException(EnumExceptionCode.NotFound, "File not found"));
                    }

                    ServerHttpResponse response = requester.getExchange().getResponse();
                    response.getHeaders().setContentType(MediaType.parseMediaType(file.getMimeType()));
                    // Set filename
                    response.getHeaders().set("Content-Disposition", "inline; filename=" + file.getName());
                    response.getHeaders().setContentLength(path.toFile().length());

                    // Cache control
                    CacheControl cacheControl = CacheControl.maxAge(Duration.ofMinutes(10));
                    if (file.getScope() == FileScope.PUBLIC) {
                        cacheControl.cachePublic();
                    } else {
                        cacheControl.cachePrivate();
                    }
                    response.getHeaders().setCacheControl(cacheControl);

                    // Check modified
                    String etag = file.getMd5() != null ? file.getMd5() : file.getFileId().getId();
                    Instant lastModified = Instant.ofEpochMilli(path.toFile().lastModified());
                    if (requester.getExchange().checkNotModified(etag, lastModified)) {
                        response.setStatusCode(HttpStatus.NOT_MODIFIED);
                        return Mono.empty();
                    }
                    return response.writeWith(DataBufferUtils.read(path, new DefaultDataBufferFactory(), 4096));
                });
    }

    @Override
    public boolean reachable(File file) {
        return Files.exists(storagePath.resolve(file.getPath()).resolve(file.getFileId().getId()));
    }

    @Override
    public Mono<Void> delete(File file) {
        return Mono.just(Path.of(file.getPath()))
                .map(path -> storagePath.resolve(path).resolve(file.getFileId().getId()))
                .flatMap(path -> blockingOperation(() -> Files.deleteIfExists(path)))
                .then();
    }
}
