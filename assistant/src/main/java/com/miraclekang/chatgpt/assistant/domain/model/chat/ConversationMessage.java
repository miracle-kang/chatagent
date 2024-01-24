package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.model.SoftDeleteEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "UK_message_id", columnList = "messageId", unique = true),
        @Index(name = "IDX_conversation_id", columnList = "conversationId")
})
@Where(clause = "deleted=false")
@SQLDelete(sql = "update conversation_message set deleted=true where id=?")
public class ConversationMessage extends SoftDeleteEntity {

    // @Comment("Message ID")
    @AttributeOverride(name = "id", column = @Column(name = "messageId", length = 64, nullable = false))
    private ConversationMessageId messageId;
    // @Comment("Conversation ID")
    @AttributeOverride(name = "id", column = @Column(name = "conversationId", length = 64))
    private ConversationId conversationId;

    // @Comment("Message")
    private Message message;

    // @Comment("Context size(Sent history messages)")
    private Integer contextSize;
    // @Comment("Charged token")
    private Integer usedToken;

    // @Comment("Message status")
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private MessageStatus status;
    // @Comment("Message sent error info")
    private String errorInfo;

    public ConversationMessage(ConversationMessageId messageId, ConversationId conversationId,
                               Message message, MessageStatus status) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.message = message;

        this.status = status;
    }

    public Long dbId() {
        return super.getId();
    }

    public void success(Integer contextSize, Integer usedToken) {
        this.contextSize = contextSize;
        this.usedToken = usedToken;
        this.status = MessageStatus.Succeeded;
    }

    public void failed(String errorInfo) {
        this.status = MessageStatus.Failed;
        this.errorInfo = errorInfo;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return super.getCreatedAt();
    }
}
