package com.miraclekang.chatgpt.subscription.application.subscription.querystack;

import com.miraclekang.chatgpt.subscription.domain.model.equity.ChatModel;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.Subscription;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionLevel;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SubscriptionDTO {

    @Schema(description = "Subscription id")
    private String subscriptionId;

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

    public SubscriptionDTO(String subscriptionId, String name, SubscriptionLevel level, SubscriptionType type,
                           Boolean disabled, Long maxTokensPerMonth, Long maxTokensPerDay,
                           Long maxTokensPerRequest, List<ChatModel> chatModels) {
        this.subscriptionId = subscriptionId;
        this.name = name;
        this.level = level;
        this.type = type;
        this.disabled = disabled;
        this.maxTokensPerMonth = maxTokensPerMonth;
        this.maxTokensPerDay = maxTokensPerDay;
        this.maxTokensPerRequest = maxTokensPerRequest;
        this.chatModels = chatModels;
    }

    public static SubscriptionDTO from(Subscription subscription) {
        return new SubscriptionDTO(
                subscription.getSubscriptionId().getId(),
                subscription.getName(),
                subscription.getLevel(),
                subscription.getType(),
                subscription.getDisabled(),
                subscription.getMaxTokensPerMonth(),
                subscription.getMaxTokensPerDay(),
                subscription.getMaxTokensPerRequest(),
                subscription.getChatModels()
        );
    }
}
