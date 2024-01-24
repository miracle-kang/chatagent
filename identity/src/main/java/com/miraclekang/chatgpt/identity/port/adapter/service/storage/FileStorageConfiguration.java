package com.miraclekang.chatgpt.identity.port.adapter.service.storage;

import com.miraclekang.chatgpt.identity.domain.model.file.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfiguration {

    @Bean
    @ConditionalOnMissingBean(FileStorageService.class)
    public DirectoryFileStorageService directoryFileService(@Value("${file.directory.storage-path}") String storagePath) {
        return new DirectoryFileStorageService(storagePath);
    }

}
