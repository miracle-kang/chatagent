package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConversationArchiveRepository extends JpaRepository<ConversationArchive, Long>,
        JpaSpecificationExecutor<ConversationArchive> {


    ConversationArchive findByArchiveIdAndOwnerUserId(ConversationArchiveId archiveId, UserId ownerUserId);
}
