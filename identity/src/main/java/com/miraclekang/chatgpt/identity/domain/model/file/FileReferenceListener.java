package com.miraclekang.chatgpt.identity.domain.model.file;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class FileReferenceListener {

    private final FileRepository fileRepository;

    public FileReferenceListener(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onFileBounded(FileBounded event) {
        event.increaseVersion();
        File file = fileRepository.findByFileId(event.getFileId());
        if (file == null) {
            throw new RuntimeException("File not found");
        }

        file.bind();
        fileRepository.save(file);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onFileReleased(FileReleased event) {
        event.increaseVersion();
        File file = fileRepository.findByFileId(event.getFileId());
        if (file == null) {
            throw new RuntimeException("File not found");
        }

        file.release();
        fileRepository.save(file);
    }
}
