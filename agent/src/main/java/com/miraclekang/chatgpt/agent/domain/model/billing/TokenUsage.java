package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import lombok.Getter;

@Getter
public class TokenUsage extends ValueObject {

    private final ChatModel model;
    private final Long totalUsage;

    private final Long latestUsage;
    private final Long minuteUsage;
    private final Long hourUsage;
    private final Long dayUsage;
    private final Long monthUsage;

    public TokenUsage(ChatModel model, Long totalUsage, Long latestUsage, Long minuteUsage,
                      Long hourUsage, Long dayUsage, Long monthUsage) {
        this.model = model;
        this.totalUsage = totalUsage;
        this.latestUsage = latestUsage;
        this.minuteUsage = minuteUsage;
        this.hourUsage = hourUsage;
        this.dayUsage = dayUsage;
        this.monthUsage = monthUsage;
    }
}
