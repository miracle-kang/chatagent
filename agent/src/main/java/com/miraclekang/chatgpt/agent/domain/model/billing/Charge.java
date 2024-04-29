package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Charge extends ValueObject {

    private final LocalDateTime time;

    private final ChatModel model;
    private final Integer tokens;

    public Charge(LocalDateTime time, ChatModel model, Integer tokens) {
        this.time = time;
        this.model = model;
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return "Charge{" +
                "time=" + time +
                ", model=" + model +
                ", tokens=" + tokens +
                '}';
    }
}
