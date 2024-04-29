package com.miraclekang.chatgpt.agent.application.chat.querystack;

import com.miraclekang.chatgpt.agent.domain.model.chat.Message;
import lombok.Data;

@Data
public class MessagePartialDTO {

    private String content;

    public MessagePartialDTO(String content) {
        this.content = content;
    }

    public static MessagePartialDTO of(Message partial) {
        return new MessagePartialDTO(partial.getContent());
    }
}
