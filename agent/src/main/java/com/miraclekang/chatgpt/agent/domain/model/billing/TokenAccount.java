package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.agent.domain.model.equity.UserEquityChecker;
import com.miraclekang.chatgpt.agent.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.model.BaseEntity;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

@Getter
@Entity
@Table(indexes = {
        @Index(name = "UK_account_id", columnList = "accountId", unique = true),
        @Index(name = "IDX_account_owner_user_id", columnList = "ownerUserId")
})
public class TokenAccount extends BaseEntity {

    // @Comment("Account ID")
    @AttributeOverride(name = "id", column = @Column(name = "accountId", length = 64, nullable = false))
    private TokenAccountId accountId;

    // @Comment("Owner User")
    @AttributeOverride(name = "id", column = @Column(name = "ownerUserId", length = 64, nullable = false))
    private UserId ownerUserId;

    // @Comment("Chat model")
    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private ChatModel model;

    // @Comment("Total tokens")
    private Long totalTokens = 0L;

    // @Comment("Total used tokens")
    private Long usedTokens = 0L;
    // @Comment("Total paid tokens")
    private Long paidTokens = 0L;

    // @Comment("Latest token used time")
    private LocalDateTime latestTokenUsedTime;

    // @Comment("Latest token usage")
    private Long latestTokenUsage;

    @Getter(AccessLevel.PRIVATE)
    // @Comment("Current minute tokens usage")
    private Long minuteTokenUsage = 0L;

    @Getter(AccessLevel.PRIVATE)
    // @Comment("Current hour tokens usage")
    private Long hourTokenUsage = 0L;

    @Getter(AccessLevel.PRIVATE)
    // @Comment("Today tokens usage")
    private Long dayTokenUsage = 0L;

    @Getter(AccessLevel.PRIVATE)
    // @Comment("Current month tokens usage")
    private Long monthTokenUsage = 0L;

    protected TokenAccount() {
    }

    public TokenAccount(TokenAccountId accountId, UserId ownerUserId, ChatModel model) {
        this.accountId = accountId;
        this.ownerUserId = ownerUserId;
        this.model = model;
    }

    public void addTokens(Long tokens) {
        this.totalTokens += tokens;
    }

    public Long availableTokens() {
        return this.totalTokens - this.paidTokens;
    }

    TokenCharge chargeToken(Token token, UserEquityChecker equityChecker) {

        // Check if the used token need to pay
        boolean needToPay = equityChecker.needToPay(this.getModel(), token, this);

        long tokens = token.numTokens();
        this.usedTokens += tokens;
        if (needToPay) {
            this.paidTokens += tokens;
        }

        countMinuteTokenUsage(tokens);
        countHourTokenUsage(tokens);
        countDayTokenUsage(tokens);
        countMonthTokenUsage(tokens);

        this.latestTokenUsage = tokens;
        this.latestTokenUsedTime = LocalDateTime.now();

        return new TokenCharge(
                new TokenChargeId(IdentityGenerator.nextIdentity()),
                this.accountId,
                this.latestTokenUsedTime,
                this.getModel(),
                token.numTokens(),
                needToPay
        );
    }

    public TokenUsage tokenUsage() {
        return new TokenUsage(
                this.getModel(),
                this.getUsedTokens(),
                this.latestTokenUsage,
                this.calcMinuteTokensUsage(),
                this.calcHourTokensUsage(),
                this.calcDayTokensUsage(),
                this.calcMonthTokensUsage()
        );
    }

    private Long calcMinuteTokensUsage() {
        if (latestTokenUsedTime == null
                || diffTimeOfDuration(this.latestTokenUsedTime, LocalDateTime.now(), Duration.ofMinutes(1))) {
            return 0L;
        }
        return this.minuteTokenUsage;
    }

    private Long calcHourTokensUsage() {
        if (latestTokenUsedTime == null
                || diffTimeOfDuration(this.latestTokenUsedTime, LocalDateTime.now(), Duration.ofHours(1))) {
            return 0L;
        }
        return this.hourTokenUsage;
    }

    private Long calcDayTokensUsage() {
        if (latestTokenUsedTime == null
                || !this.latestTokenUsedTime.toLocalDate().isEqual(LocalDateTime.now().toLocalDate())) {
            return 0L;
        }
        return this.dayTokenUsage;
    }

    private Long calcMonthTokensUsage() {
        if (latestTokenUsedTime == null
                || !YearMonth.from(this.latestTokenUsedTime).equals(YearMonth.from(LocalDateTime.now()))) {
            return 0L;
        }
        return this.monthTokenUsage;
    }

    private void countMinuteTokenUsage(Long tokens) {
        if (this.latestTokenUsedTime == null ||
                diffTimeOfDuration(this.latestTokenUsedTime, LocalDateTime.now(), Duration.ofMinutes(1))) {
            this.minuteTokenUsage = tokens;
        } else {
            this.minuteTokenUsage += tokens;
        }
    }

    private void countHourTokenUsage(Long tokens) {
        if (this.latestTokenUsedTime == null ||
                diffTimeOfDuration(this.latestTokenUsedTime, LocalDateTime.now(), Duration.ofHours(1))) {
            this.hourTokenUsage = tokens;
        } else {
            this.hourTokenUsage += tokens;
        }
    }

    private void countDayTokenUsage(Long tokens) {
        if (this.latestTokenUsedTime == null ||
                !this.latestTokenUsedTime.toLocalDate().isEqual(LocalDateTime.now().toLocalDate())) {
            this.dayTokenUsage = tokens;
        } else {
            this.dayTokenUsage += tokens;
        }
    }

    private void countMonthTokenUsage(Long tokens) {
        if (this.latestTokenUsedTime == null ||
                !YearMonth.from(this.latestTokenUsedTime).equals(YearMonth.from(LocalDateTime.now()))) {
            this.monthTokenUsage = tokens;
        } else {
            this.monthTokenUsage += tokens;
        }
    }

    private boolean diffTimeOfDuration(LocalDateTime time1, LocalDateTime time2, Duration duration) {
        long durationMillis = duration.toMillis();
        long time1Millis = time1.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long time2Millis = time2.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return time1Millis - time1Millis % durationMillis != time2Millis - time2Millis % durationMillis;
    }
}
