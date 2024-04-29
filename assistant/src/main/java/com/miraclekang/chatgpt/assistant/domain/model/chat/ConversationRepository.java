package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConversationRepository extends JpaRepository<Conversation, Long>,
        JpaSpecificationExecutor<Conversation> {

    Conversation findByConversationId(ConversationId conversationId);


    Conversation findByConversationIdAndOwnerUserId(ConversationId conversationId, UserId ownerUserId);

    Boolean existsByConversationIdAndOwnerUserId(ConversationId conversationId, UserId ownerUserId);
}
