package com.miraclekang.chatgpt.subscription.domain.model.equity.subscription;

import com.miraclekang.chatgpt.subscription.domain.Converters;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.model.AggregateRoot;
import com.miraclekang.chatgpt.subscription.domain.model.equity.ChatModel;
import com.miraclekang.chatgpt.subscription.domain.model.equity.Equity;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityId;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityLimitation;
import com.miraclekang.chatgpt.subscription.domain.model.identity.OperatorUser;
import jakarta.persistence.*;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 按周期订阅权益
 */
@Getter
@Entity
@Table(indexes = {
        @Index(name = "UK_subscription_id", columnList = "subscriptionId")
})
public class Subscription extends AggregateRoot implements Equity {

    // @Comment("Subscription ID")
    @AttributeOverride(name = "id", column = @Column(name = "subscriptionId", length = 64, nullable = false))
    private SubscriptionId subscriptionId;

    // @Comment("Subscription name")
    private String name;

    // @Comment("Subscription level")
    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private SubscriptionLevel level;

    // @Comment("Subscription type")
    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private SubscriptionType type;

    // @Comment("Disabled")
    private Boolean disabled;

    // @Comment("Max tokens per month")
    private Long maxTokensPerMonth;
    // @Comment("Max tokens per day")
    private Long maxTokensPerDay;
    // @Comment("Max tokens per request")
    private Long maxTokensPerRequest;

    @Lob
    // @Comment("Chat models could be used")
    @Convert(converter = Converters.ChatModelListConverter.class)
    private List<ChatModel> chatModels;

    // @Comment("Operator user")
    @AttributeOverrides( value = {
            @AttributeOverride(name = "userId", column = @Column(name = "operatorUserId", length = 64)),
            @AttributeOverride(name = "userName", column = @Column(name = "operatorUserName", length = 128)),
            @AttributeOverride(name = "userIp", column = @Column(name = "operatorUserIp", length = 64))
    })
    private OperatorUser operator;

    protected Subscription() {
    }

    public Subscription(SubscriptionId subscriptionId, String name, SubscriptionLevel level, SubscriptionType type,
                        Boolean disabled, Long maxTokensPerMonth, Long maxTokensPerDay,
                        Long maxTokensPerRequest, List<ChatModel> chatModels, Requester requester) {
        this.subscriptionId = subscriptionId;
        this.name = name;
        this.level = level;
        this.type = type;
        this.disabled = disabled;
        this.maxTokensPerMonth = maxTokensPerMonth;
        this.maxTokensPerDay = maxTokensPerDay;
        this.maxTokensPerRequest = maxTokensPerRequest;
        this.chatModels = chatModels;

        this.operator = OperatorUser.requester(requester);
    }

    public void update(String name, SubscriptionLevel level, SubscriptionType type,
                       Boolean disabled, Long maxTokensPerMonth, Long maxTokensPerDay,
                       Long maxTokensPerRequest, List<ChatModel> chatModels, Requester requester) {
        this.name = name;
        this.level = level;
        this.type = type;
        this.disabled = disabled;
        this.maxTokensPerMonth = maxTokensPerMonth;
        this.maxTokensPerDay = maxTokensPerDay;
        this.maxTokensPerRequest = maxTokensPerRequest;
        this.chatModels = chatModels;

        this.operator = OperatorUser.requester(requester);
    }

    @Override
    public EquityId equityId() {
        return subscriptionId.toEquityId();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String unit() {
        return switch (type) {
            case DAILY -> "Days";
            case WEEKLY -> "Weeks";
            case MONTHLY -> "Months";
            case YEARLY -> "Years";
        };
    }

//    @Override
//    public EquityModelRule modelRule() {
//        // The model rule is different for different subscription level
//        return (equity, model) -> this.available() && chatModels.contains(model);
//    }
//
//    @Override
//    public Function<ChatModel, Integer> maxTokenRule() {
//        return model -> maxTokensPerRequest == null ? model.getMaxTokens() : maxTokensPerRequest.intValue();
//    }
//
//    @Override
//    public EquityTokenRule tokenRule() {
//        // The token rule is different for different subscription level and type
//        return (equity, model, token, tokenAccount) -> {
//            if (!available() || !chatModels.contains(model)) {
//                return false;
//            }
//            long numTokens = token.numTokens();
//
//            // Check token account has enough tokens could to pay
//            if (tokenAccount.availableTokens() >= numTokens) {
//                return true;
//            }
//
//            TokenUsage usage = tokenAccount.tokenUsage();
//
//            // Check request token number is not greater than max tokens per request
//            if (maxTokensPerRequest != null && numTokens > maxTokensPerRequest) {
//                return false;
//            }
//            // Check request token number is not greater than max tokens per day
//            if (maxTokensPerDay != null && usage.getDayUsage() + numTokens > maxTokensPerDay) {
//                return false;
//            }
//            // Check request token number is not greater than max tokens per month
//            if (maxTokensPerMonth != null && usage.getMonthUsage() + numTokens > maxTokensPerMonth) {
//                return false;
//            }
//
//            // Default it is could use token
//            return true;
//        };
//    }
//
//    @Override
//    public EquityPayableRule payableRule() {
//        return (equity, mode, usedToken, account) -> {
//            if (!available()) {
//                return true;
//            }
//            long numTokens = usedToken.numTokens();
//
//            TokenUsage usage = account.tokenUsage();
//
//            // Check request usedToken number is over than max tokens per month limit
//            if (maxTokensPerMonth != null && usage.getMonthUsage() + numTokens > maxTokensPerMonth) {
//                return true;
//            }
//
//            // Check request usedToken number is over than max tokens per day limit
//            if (maxTokensPerDay != null && usage.getDayUsage() + numTokens > maxTokensPerDay) {
//                return true;
//            }
//
//            // Default is not to pay
//            return false;
//        };
//    }

    @Override
    public LocalDateTime expiresTime(LocalDateTime startTime, BigDecimal quantity) {
        LocalDate expiresDate = startTime.plus(quantity.longValue(), type.chronoUnit())
                .toLocalDate();
        if (expiresDate.isBefore(LocalDate.now())) {
            expiresDate = LocalDate.now();
        }
        if (startTime.toLocalTime().isAfter(LocalTime.MIN)) {
            expiresDate = expiresDate.plusDays(1);
        }
        return expiresDate.atStartOfDay();
    }

    @Override
    public EquityLimitation limitation() {
        return new EquityLimitation(
                effective(),
                maxTokensPerMonth,
                maxTokensPerDay,
                maxTokensPerRequest,
                chatModels
        );
    }

    public boolean effective() {
        return BooleanUtils.isNotTrue(disabled);
    }
}
