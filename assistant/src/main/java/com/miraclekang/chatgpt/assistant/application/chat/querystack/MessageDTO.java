package com.miraclekang.chatgpt.assistant.application.chat.querystack;

import com.miraclekang.chatgpt.assistant.domain.model.chat.ConversationMessage;
import com.miraclekang.chatgpt.assistant.domain.model.chat.MessageRole;
import com.miraclekang.chatgpt.assistant.domain.model.chat.MessageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class MessageDTO {

    @Schema(description = "Message ID")
    private String messageId;

    @Schema(description = "Message time")
    private ZonedDateTime time;

    @Schema(description = "Message role")
    private MessageRole role;

    @Schema(description = "Message content")
    private String content;

    @Schema(description = "Context size(Sent history messages)")
    private Integer contextSize;
    @Schema(description = "Charged token")
    private Integer usedToken;
    @Schema(description = "Message status")
    private MessageStatus status;

    @Schema(description = "Message error info")
    private String errorInfo;

    public MessageDTO(String messageId, ZonedDateTime time, MessageRole role, String content,
                      Integer contextSize, Integer usedToken, MessageStatus status, String errorInfo) {
        this.messageId = messageId;
        this.time = time;
        this.role = role;
        this.content = content;

        this.contextSize = contextSize;
        this.usedToken = usedToken;
        this.status = status;
        this.errorInfo = errorInfo;
    }

    public static MessageDTO of(ConversationMessage message) {
        if (message == null) {
            return null;
        }
        return new MessageDTO(
                message.getMessageId().getId(),
                message.getCreatedAt().atZone(ZoneId.systemDefault()),
                message.getMessage().getRole(),
                message.getMessage().getContent(),
                message.getContextSize(),
                message.getUsedToken(),
                message.getStatus(),
                message.getErrorInfo()
        );
    }
}
