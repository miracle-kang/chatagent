package com.miraclekang.chatgpt.identity.port.adapter.restapi.admin;


import com.miraclekang.chatgpt.common.restapi.SearchCriteriaParam;
import com.miraclekang.chatgpt.identity.application.common.FileService;
import com.miraclekang.chatgpt.identity.application.common.querystack.FileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/files")
@Tag(name = "File management APIs", description = "File management APIs")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Query files", description = "Query files")
    public Mono<Page<FileDTO>> queryFiles(@ParameterObject SearchCriteriaParam criteriaParam,
                                          @ParameterObject Pageable pageable) {
        return fileService.queryFiles(criteriaParam.toCriteria(), pageable);
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Get a file", description = "Get a file")
    public Mono<FileDTO> getFile(@PathVariable String fileId) {
        return fileService.getFile(fileId);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete a file", description = "Delete a file")
    public Mono<Void> deleteFile(@PathVariable String fileId) {
        return fileService.deleteFile(fileId);
    }
}
