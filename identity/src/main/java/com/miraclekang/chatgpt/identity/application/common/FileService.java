package com.miraclekang.chatgpt.identity.application.common;

import com.miraclekang.chatgpt.common.exception.ApplicationException;
import com.miraclekang.chatgpt.identity.application.common.querystack.FileDTO;
import com.miraclekang.chatgpt.identity.domain.EnumExceptionCode;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import com.miraclekang.chatgpt.identity.domain.model.file.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public FileService(FileRepository fileRepository, FileStorageService fileStorageService) {
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
    }

    public Mono<FileDTO> uploadFile(FileType type, FileScope scope, FilePart aFilePart) {
        return Requester.currentRequester()
                .filter(requester -> scope == FileScope.PUBLIC || requester.isAuthenticated())
                .switchIfEmpty(Mono.error(new ApplicationException(EnumExceptionCode.Unauthorized)))
                .flatMap(requester -> Mono.just(aFilePart)
                        .publishOn(Schedulers.boundedElastic())
                        .flatMap(filePart -> fileStorageService.upload(requester, type, scope, filePart))
                        .switchIfEmpty(Mono.error(new RuntimeException("File upload failed")))
                        .map(fileRepository::save)
                        .map(file -> toFileDTO(file, requester))
                );
    }

    public Mono<FileDTO> getFile(String aFileId) {
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(new FileId(aFileId))
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(fileRepository::findByFileId)
                        .switchIfEmpty(Mono.error(new ApplicationException("File not found", EnumExceptionCode.NotFound)))
                        .filter(file -> file.canAccess(requester))
                        .switchIfEmpty(Mono.error(new ApplicationException(EnumExceptionCode.Forbidden)))
                        .map(file -> toFileDTO(file, requester))
                );
    }

    public Mono<Void> previewFile(String aFileId) {
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(new FileId(aFileId))
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(fileRepository::findByFileId)
                        .switchIfEmpty(Mono.error(new ApplicationException("File not found", EnumExceptionCode.NotFound)))
                        .filter(file -> file.canAccess(requester))
                        .switchIfEmpty(Mono.error(new ApplicationException(EnumExceptionCode.Forbidden)))
                        .flatMap(file -> fileStorageService.preview(file, requester))
                );
    }

    public Mono<Void> deleteFile(String aFileId) {
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(new FileId(aFileId))
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(fileRepository::findByFileId)
                        .switchIfEmpty(Mono.error(new ApplicationException("File not found", EnumExceptionCode.NotFound)))
                        .filter(file -> file.canAccess(requester))
                        .switchIfEmpty(Mono.error(new ApplicationException(EnumExceptionCode.Forbidden)))
                        .flatMap(file -> {
                            if (file.status(fileStorageService) == FileStatus.BOUNDED) {
                                return Mono.error(new ApplicationException("File is in use", EnumExceptionCode.BadRequest));
                            }
                            return file.delete(fileStorageService)
                                    .then(blockingOperation(() -> fileRepository.save(file)))
                                    .then();
                        })
                );
    }

    public Mono<Page<FileDTO>> queryFiles(List<SearchCriteria> searchCriteria, Pageable pageable) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(File_.ID).descending());

                    return fileRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest)
                            .map(file -> toFileDTO(file, requester));
                });
    }

    private FileDTO toFileDTO(File file, Requester requester) {
        return new FileDTO(
                file.getFileId().getId(),
                file.getName(),
                file.getSize(),
                file.status(fileStorageService),
                file.previewUrl(fileStorageService, requester)
        );
    }
}
