package com.miraclekang.chatgpt.agent.application.chat.command;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NewMessageCommand {

    @Schema(description = "Message content")
    private String content;
}
