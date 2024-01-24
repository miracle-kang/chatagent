package com.miraclekang.chatgpt.identity.application.common.querystack;

import com.miraclekang.chatgpt.identity.domain.model.file.FileStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDTO {

    @Schema(description = "File ID")
    private String fileId;
    @Schema(description = "File name")
    private String filename;
    @Schema(description = "File size")
    private Long size;

    @Schema(description = "File status")
    private FileStatus status;

    @Schema(description = "File preview url")
    private String previewUrl;

    public FileDTO(String fileId, String filename, Long size, FileStatus status, String previewUrl) {
        this.fileId = fileId;
        this.filename = filename;
        this.size = size;
        this.status = status;
        this.previewUrl = previewUrl;
    }
}
