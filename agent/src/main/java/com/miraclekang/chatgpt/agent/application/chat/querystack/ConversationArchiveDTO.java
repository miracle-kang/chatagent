package com.miraclekang.chatgpt.agent.application.chat.querystack;

import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.agent.domain.model.chat.ConversationArchive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConversationArchiveDTO extends ConversationDTO {

    @Schema(description = "Conversation Archive ID")
    private String archiveId;

    public ConversationArchiveDTO(String archiveId, String conversationId, String ownerUserId, String name,
                                  Boolean sendHistory, ChatModel model, Double temperature, Double topP, Integer maxTokens) {
        super(conversationId, ownerUserId, name, sendHistory, model, temperature, topP, maxTokens);
        this.archiveId = archiveId;
    }

    public static ConversationArchiveDTO of(ConversationArchive archive) {
        if (archive == null) {
            return null;
        }

        return new ConversationArchiveDTO(
                archive.getArchiveId().getId(),
                archive.getConversationId().getId(),
                archive.getOwnerUserId().getId(),
                archive.getName(),
                archive.getSendHistory(),
                archive.getModel(),
                archive.getTemperature(),
                archive.getTopP(),
                archive.getMaxTokens()
        );
    }
}
