package com.miraclekang.chatgpt.assistant.domain.model.billing;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

import java.util.List;

@Getter
public class TokenBilling extends ValueObject {

    private final TokenAccountId accountId;

    private final List<Charge> chargeList;

    public TokenBilling(TokenAccountId accountId, List<Charge> chargeList) {
        this.accountId = accountId;
        this.chargeList = chargeList;
    }
}
