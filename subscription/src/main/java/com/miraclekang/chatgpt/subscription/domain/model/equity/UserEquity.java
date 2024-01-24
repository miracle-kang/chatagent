package com.miraclekang.chatgpt.subscription.domain.model.equity;

import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.model.AggregateRoot;
import com.miraclekang.chatgpt.subscription.domain.model.identity.OperatorUser;
import com.miraclekang.chatgpt.subscription.domain.model.identity.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Entity
@Table(indexes = {
        @Index(name = "UK_user_equity_id", columnList = "userEquityId", unique = true),
        @Index(name = "IDX_equity_owner_user_id", columnList = "ownerUserId"),
        @Index(name = "IDX_equity_id", columnList = "equityId,equityType")
})
public class UserEquity extends AggregateRoot {

    // @Comment("User equity ID")
    @AttributeOverride(name = "id", column = @Column(name = "userEquityId", length = 64, nullable = false))
    private UserEquityId userEquityId;

    // @Comment("User ID")
    @AttributeOverride(name = "id", column = @Column(name = "ownerUserId", length = 64, nullable = false))
    private UserId ownerUserId;
    // @Comment("Equity ID")
    private EquityId equityId;

    // @Comment("Equity name")
    private String equityName;
    // @Comment("Equity quantity")
    private BigDecimal quantity;
    // @Comment("Equity uint")
    private String unit;

    // @Comment("Effective time")
    private LocalDateTime effectiveTime;
    // @Comment("Expires time")
    private LocalDateTime expiresTime;

    // @Comment("Operator")
    @AttributeOverrides( value = {
            @AttributeOverride(name = "userId", column = @Column(name = "operatorUserId", length = 64)),
            @AttributeOverride(name = "userName", column = @Column(name = "operatorUserName", length = 128)),
            @AttributeOverride(name = "userIp", column = @Column(name = "operatorUserIp", length = 64))
    })
    private OperatorUser operator;

    @Transient
    @Getter(AccessLevel.PRIVATE)
    private transient Equity equity;

    protected UserEquity() {
    }

    UserEquity(UserEquityId userEquityId,
               UserId ownerUserId, EquityId equityId,
               String equityName,
               BigDecimal quantity, String unit,
               LocalDateTime effectiveTime,
               LocalDateTime expiresTime,
               Requester requester) {
        this.userEquityId = userEquityId;
        this.ownerUserId = ownerUserId;
        this.equityId = equityId;
        this.equityName = equityName;
        this.quantity = quantity;
        this.unit = unit;
        this.effectiveTime = effectiveTime;
        this.expiresTime = expiresTime;

        this.operator = OperatorUser.currentOrSystemOperator(requester);
    }

    public boolean available() {
        return status() == UserEquityStatus.AVAILABLE;
    }

    public UserEquityStatus status() {
        if (LocalDateTime.now().isBefore(effectiveTime)) {
            return UserEquityStatus.INEFFECTIVE;
        }
        if (LocalDateTime.now().isAfter(expiresTime)) {
            return UserEquityStatus.EXPIRES;
        }
        return UserEquityStatus.AVAILABLE;
    }

    public boolean couldRenewal(Equity equity) {
        return available() && equity.equityId().equals(this.getEquityId());
    }

    void renewal(Equity equity, BigDecimal quantity) {
        if (!available()) {
            throw new IllegalStateException("User equity not available");
        }
        if (!equity.equityId().equals(this.getEquityId())) {
            throw new IllegalArgumentException("Equity ID did not match");
        }

        this.equityName = equity.name();
        this.unit = equity.unit();
        this.quantity = this.quantity.add(quantity);
        this.expiresTime = equity.expiresTime(this.expiresTime, quantity);
    }

    public void renewal(EquityRepositoryDelegate equityRepository, BigDecimal quantity) {
        if (!available()) {
            throw new IllegalStateException("User equity not available");
        }

        Equity equity = equityRepository.getEquity(getEquityId());
        if (equity == null) {
            throw new IllegalArgumentException("Equity ID did not match");
        }
        this.renewal(equity, quantity);
    }

    UserEquity initEquity(EquityRepositoryDelegate equityRepository) {
        if (equity != null) {
            return this;
        }
        equity = equityRepository.getEquity(getEquityId());
        if (equity == null) {
            log.warn(">>>>>> Equity {} not found", getEquityId());
        }
        return this;
    }

//    boolean allowUseModel(ChatModel chatModel) {
//        if (!available()) {
//            return false;
//        }
//        if (equity != null) {
//            return equity.modelRule().satisfied(this, chatModel);
//        }
//
//        log.warn(">>>>>> Equity {} not exists", getEquityId());
//        return false;
//    }
//
//    int allowedMaxToken(ChatModel model) {
//        if (!available()) {
//            return 0;
//        }
//        if (equity != null) {
//            return equity.maxTokenRule().apply(model);
//        }
//
//        log.warn(">>>>>> Equity {} not found", getEquityId());
//        return 0;
//    }

//    boolean couldUseToken(ChatModel model, Token newToken, TokenAccount tokenAccount) {
//        if (!available()) {
//            return false;
//        }
//        if (equity != null) {
//            return equity.tokenRule().satisfied(this, model, newToken, tokenAccount);
//        }
//
//        log.warn(">>>>>> Equity {} not found", getEquityId());
//        return false;
//    }
//
//    boolean needToPay(ChatModel chatModel, Token token, TokenAccount account) {
//        if (!available()) {
//            return true;
//        }
//        if (equity != null) {
//            return equity.payableRule().satisfied(this, chatModel, token, account);
//        }
//
//        log.warn(">>>>>> Equity {} not found", getEquityId());
//        return true;
//    }

    public UserEquityInfo equityInfo(EquityRepositoryDelegate equityRepository) {
        Equity equity = equityRepository.getEquity(getEquityId());
        return new UserEquityInfo(
                userEquityId,
                equityId,
                equityName,
                quantity,
                unit,
                effectiveTime,
                expiresTime,
                status(),
                equity == null ? null : equity.limitation()
        );
    }
}
