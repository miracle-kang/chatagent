package com.miraclekang.chatgpt.agent.application.chat.querystack;

import com.miraclekang.chatgpt.agent.domain.model.billing.TokenUsage;
import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TokenAccountDTO {

    @Schema(description = "Token account id")
    private String accountId;

    @Schema(description = "Token account owner user id")
    private String ownerUserId;
    @Schema(description = "Token account owner username")
    private String ownerUsername;

    @Schema(description = "Chat model")
    private ChatModel model;
    @Schema(description = "Total tokens")
    private Long totalTokens;
    @Schema(description = "Total paid tokens")
    private Long paidTokens;

    @Schema(description = "Latest token used time")
    private LocalDateTime latestTokenUsedTime;
    @Schema(description = "Current tokens usage")
    private TokenUsage tokenUsage;

    public TokenAccountDTO(String accountId, String ownerUserId, String ownerUsername,
                           ChatModel model, Long totalTokens, Long paidTokens,
                           LocalDateTime latestTokenUsedTime, TokenUsage tokenUsage) {
        this.accountId = accountId;
        this.ownerUserId = ownerUserId;
        this.ownerUsername = ownerUsername;
        this.model = model;
        this.totalTokens = totalTokens;
        this.paidTokens = paidTokens;
        this.latestTokenUsedTime = latestTokenUsedTime;
        this.tokenUsage = tokenUsage;
    }
}
