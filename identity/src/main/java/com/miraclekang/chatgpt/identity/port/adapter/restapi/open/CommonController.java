package com.miraclekang.chatgpt.identity.port.adapter.restapi.open;

import com.miraclekang.chatgpt.identity.application.common.FileService;
import com.miraclekang.chatgpt.identity.application.common.SystemProfileService;
import com.miraclekang.chatgpt.identity.application.common.querystack.FileDTO;
import com.miraclekang.chatgpt.identity.application.common.querystack.SystemProfileDTO;
import com.miraclekang.chatgpt.identity.domain.model.file.FileScope;
import com.miraclekang.chatgpt.identity.domain.model.file.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/open/common")
@Tag(name = "Common APIs", description = "Common APIs")
public class CommonController {

    private final FileService fileService;
    private final SystemProfileService systemProfileService;

    public CommonController(FileService fileService,
                            SystemProfileService systemProfileService) {
        this.fileService = fileService;
        this.systemProfileService = systemProfileService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file", description = "Upload file")
    public Mono<FileDTO> uploadFile(@RequestParam FileType type,
                                    @RequestParam FileScope scope,
                                    @RequestPart("file") Mono<FilePart> file) {
        return file.flatMap(filePart -> fileService.uploadFile(type, scope, filePart));
    }

    @GetMapping("/files/{fileId}")
    @Operation(summary = "Get file", description = "Get file")
    public Mono<FileDTO> getFile(@PathVariable String fileId) {
        return fileService.getFile(fileId);
    }

    @GetMapping("/files/{fileId}/preview")
    @Operation(summary = "Preview a file", description = "Preview a file")
    public Mono<Void> previewFile(@PathVariable String fileId) {
        return fileService.previewFile(fileId);
    }

    @GetMapping("/system/profile")
    @Operation(summary = "Get system profile", description = "Get system profile")
    public Mono<SystemProfileDTO> getSystemProfile() {
        return systemProfileService.get();
    }
}
