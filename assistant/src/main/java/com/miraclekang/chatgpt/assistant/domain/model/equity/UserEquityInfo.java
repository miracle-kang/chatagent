package com.miraclekang.chatgpt.assistant.domain.model.equity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.assistant.domain.model.billing.Token;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccount;
import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
public class UserEquityInfo extends ValueObject {

    private final UserEquityId userEquityId;
    private final EquityId equityId;
    private final String equityName;
    private final BigDecimal quantity;
    private final String unit;

    private final LocalDateTime effectiveTime;
    private final LocalDateTime expiresTime;
    private final UserEquityStatus status;

    private final EquityLimitation limitation;

    public UserEquityInfo(UserEquityId userEquityId, EquityId equityId, String equityName,
                          BigDecimal quantity, String unit,
                          LocalDateTime effectiveTime,
                          LocalDateTime expiresTime,
                          UserEquityStatus status, EquityLimitation limitation) {
        this.userEquityId = userEquityId;
        this.equityId = equityId;
        this.equityName = equityName;
        this.quantity = quantity;
        this.unit = unit;
        this.effectiveTime = effectiveTime;
        this.expiresTime = expiresTime;
        this.status = status;
        this.limitation = limitation;
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

    boolean allowUseModel(ChatModel chatModel) {
        if (!available()) {
            return false;
        }
        if (limitation != null) {
            return limitation.canUse(chatModel);
        }

        log.warn(">>>>>> Equity {} not exists", getEquityId());
        return false;
    }

    int allowedMaxToken(ChatModel model) {
        if (!available()) {
            return 0;
        }
        if (limitation != null) {
            return limitation.maxToken(model);
        }

        log.warn(">>>>>> Equity {} not found", getEquityId());
        return 0;
    }

    boolean couldUseToken(ChatModel model, Token newToken, TokenAccount tokenAccount) {
        if (!available()) {
            return false;
        }
        if (limitation != null) {
            return limitation.couldUseToken(model, newToken, tokenAccount);
        }

        log.warn(">>>>>> Equity {} not found", getEquityId());
        return false;
    }

    boolean needToPay(ChatModel chatModel, Token token, TokenAccount account) {
        if (!available()) {
            return true;
        }
        if (limitation != null) {
            return limitation.needToPay(chatModel, token, account);
        }

        log.warn(">>>>>> Equity {} not found", getEquityId());
        return true;
    }

}
