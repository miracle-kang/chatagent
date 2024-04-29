package com.miraclekang.chatgpt.agent.application.chat.command;

import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class NewConversationCommand {

    @Schema(description = "Conversation name")
    private String name;


    // Configuration for this conversation

    @Schema(description = "Whether to send the conversation history message.", defaultValue = "true")
    private Boolean sendHistory = true;

    @Schema(description = "ID of the model to use.", defaultValue = "GPT3_5")
    private ChatModel model = ChatModel.GPT3_5;           // default GPT3_5
    @Schema(description = "Sampling temperature to use, between 0 and 2.", defaultValue = "0.7")
    @Range(min = 0, max = 2, message = "between 0 and 2.")
    private Double temperature = 0.7;     // default 0.7
    @Schema(description = "Alternative to sampling with temperature", defaultValue = "1.0")
    private Double topP = 1.0;            // default 1
    @Schema(description = "The maximum number of tokens to generate in the chat completion.", defaultValue = "2000")
    private Integer maxTokens = 2000;      // default 2000
}
