package com.miraclekang.chatgpt.agent.domain.model.equity;

import com.miraclekang.chatgpt.agent.domain.model.billing.Token;
import com.miraclekang.chatgpt.agent.domain.model.billing.TokenAccount;
import com.miraclekang.chatgpt.agent.domain.model.billing.TokenUsage;
import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

@Getter
public class EquityLimitation extends ValueObject {

    private final Boolean effective;
    private final Long maxTokensPerDay;
    private final Long maxTokensPerMonth;
    private final Long maxTokensPerRequest;
    private final List<ChatModel> chatModels;

    public EquityLimitation(Boolean effective, Long maxTokensPerMonth, Long maxTokensPerDay,
                            Long maxTokensPerRequest, List<ChatModel> chatModels) {
        this.effective = effective;
        this.maxTokensPerMonth = maxTokensPerMonth;
        this.maxTokensPerDay = maxTokensPerDay;
        this.maxTokensPerRequest = maxTokensPerRequest;
        this.chatModels = chatModels;
    }

    public static EquityLimitation EMPTY = new EquityLimitation(
            false,
            0L,
            0L,
            0L,
            null
    );

    public boolean effective() {
        return BooleanUtils.isTrue(effective);
    }

    public boolean canUse(ChatModel model) {
        // The model rule is different for different subscription level
        return effective() && chatModels.contains(model);
    }

    public Integer maxToken(ChatModel model) {
        return maxTokensPerRequest == null ? model.getMaxTokens() : maxTokensPerRequest.intValue();
    }

    public boolean couldUseToken(ChatModel model, Token token, TokenAccount tokenAccount) {
        if (!effective() || !chatModels.contains(model)) {
            return false;
        }
        long numTokens = token.numTokens();

        // Check token account has enough tokens could to pay
        if (tokenAccount.availableTokens() >= numTokens) {
            return true;
        }

        TokenUsage usage = tokenAccount.tokenUsage();

        // Check request token number is not greater than max tokens per request
        if (maxTokensPerRequest != null && numTokens > maxTokensPerRequest) {
            return false;
        }
        // Check request token number is not greater than max tokens per day
        if (maxTokensPerDay != null && usage.getDayUsage() + numTokens > maxTokensPerDay) {
            return false;
        }
        // Check request token number is not greater than max tokens per month
        if (maxTokensPerMonth != null && usage.getMonthUsage() + numTokens > maxTokensPerMonth) {
            return false;
        }

        // Default it is could use token
        return true;
    }

    public boolean needToPay(ChatModel chatModel, Token token, TokenAccount account) {
        if (!effective()) {
            return true;
        }
        long numTokens = token.numTokens();

        TokenUsage usage = account.tokenUsage();

        // Check request usedToken number is over than max tokens per month limit
        if (maxTokensPerMonth != null && usage.getMonthUsage() + numTokens > maxTokensPerMonth) {
            return true;
        }

        // Check request usedToken number is over than max tokens per day limit
        if (maxTokensPerDay != null && usage.getDayUsage() + numTokens > maxTokensPerDay) {
            return true;
        }

        // Default is not to pay
        return false;
    }
}
