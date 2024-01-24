package com.miraclekang.chatgpt.subscription.application.subscription.command;

import com.miraclekang.chatgpt.subscription.domain.model.equity.ChatModel;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionLevel;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class NewSubscriptionCommand {

    @Schema(description = "Subscription name")
    private String name;
    @Schema(description = "Subscription level")
    private SubscriptionLevel level;
    @Schema(description = "Subscription type")
    private SubscriptionType type;

    @Schema(description = "Disabled")
    private Boolean disabled;

    @Schema(description = "Max tokens per month")
    private Long maxTokensPerMonth;
    @Schema(description = "Max tokens per day")
    private Long maxTokensPerDay;
    @Schema(description = "Max tokens per request")
    private Long maxTokensPerRequest;

    @Schema(description = "Chat models could be used")
    private List<ChatModel> chatModels;
}
