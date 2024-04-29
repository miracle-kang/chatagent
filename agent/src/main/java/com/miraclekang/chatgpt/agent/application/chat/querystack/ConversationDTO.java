package com.miraclekang.chatgpt.agent.application.chat.querystack;

import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.agent.domain.model.chat.Conversation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
public class ConversationDTO {

    @Schema(description = "Conversation ID")
    private String conversationId;
    @Schema(description = "Owner User")
    private String ownerUserId;
    @Schema(description = "Conversation name")
    private String name;

    @Schema(description = "Whether to send the conversation history message.", defaultValue = "true")
    private Boolean sendHistory;

    // Configuration for this conversation
    @Schema(description = "ID of the model to use.", defaultValue = "GPT3_5")
    private ChatModel model;           // default gpt-3.5-turbo
    @Schema(description = "Sampling temperature to use, between 0 and 2.", defaultValue = "0.7")
    @Range(min = 0, max = 2, message = "between 0 and 2.")
    private Double temperature;     // default 0.7
    @Schema(description = "Alternative to sampling with temperature", defaultValue = "1.0")
    private Double topP;            // default 1
    @Schema(description = "The maximum number of tokens to generate in the chat completion.", defaultValue = "2000")
    private Integer maxTokens;      // default 2000

    public ConversationDTO(String conversationId, String ownerUserId, String name, Boolean sendHistory,
                           ChatModel model, Double temperature, Double topP, Integer maxTokens) {
        this.conversationId = conversationId;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.sendHistory = sendHistory;
        this.model = model;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
    }

    public static ConversationDTO of(Conversation conversation) {
        if (conversation == null) {
            return null;
        }

        return new ConversationDTO(
                conversation.getConversationId().getId(),
                conversation.getOwnerUserId().getId(),
                conversation.getName(),
                conversation.getSendHistory(),
                conversation.getModel(),
                conversation.getTemperature(),
                conversation.getTopP(),
                conversation.getMaxTokens()
        );
    }
}
