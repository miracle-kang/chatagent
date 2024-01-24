package com.miraclekang.chatgpt.assistant.domain.model.billing;

import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(indexes = {
        @Index(name = "UK_token_charge_id", columnList = "chargeId", unique = true),
        @Index(name = "IDX_charge_account_id", columnList = "accountId"),
        @Index(name = "IDX_charge_time", columnList = "time"),
})
public class TokenCharge extends BaseEntity {

    // @Comment("Charge ID")
    @AttributeOverride(name = "id", column = @Column(name = "chargeId", length = 64, nullable = false))
    private TokenChargeId chargeId;
    // @Comment("Account ID")
    @AttributeOverride(name = "id", column = @Column(name = "accountId", length = 64, nullable = false))
    private TokenAccountId accountId;

    // @Comment("Charge Time")
    private LocalDateTime time;
    // @Comment("Model")
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ChatModel model;
    // @Comment("Tokens")
    private Integer tokens;

    // @Comment("is tokens paid")
    private Boolean paid = false;

    public TokenCharge() {
    }

    public TokenCharge(TokenChargeId chargeId, TokenAccountId accountId, LocalDateTime time,
                       ChatModel model, Integer tokens, Boolean paid) {
        this.chargeId = chargeId;
        this.accountId = accountId;

        this.time = time;
        this.model = model;
        this.tokens = tokens;
        this.paid = paid;
    }

    public Charge toCharge() {
        return new Charge(
                this.time,
                this.model,
                this.tokens
        );
    }
}
