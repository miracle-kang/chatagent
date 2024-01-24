package com.miraclekang.chatgpt.identity.domain.model.equity;

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
    private final List<String> chatModels;

    public EquityLimitation(Boolean effective, Long maxTokensPerMonth, Long maxTokensPerDay,
                            Long maxTokensPerRequest, List<String> chatModels) {
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
}
