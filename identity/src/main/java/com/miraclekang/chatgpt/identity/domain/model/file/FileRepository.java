package com.miraclekang.chatgpt.identity.domain.model.file;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long>,
        JpaSpecificationExecutor<File> {

    File findByFileId(FileId fileId);
}
