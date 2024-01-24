package com.miraclekang.chatgpt.subscription.domain.model.equity;


import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.subscription.domain.model.identity.UserId;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EquityGrantService {

    private final UserEquityRepository userEquityRepository;

    public EquityGrantService(UserEquityRepository userEquityRepository) {
        this.userEquityRepository = userEquityRepository;
    }

    /**
     * Grant equity to user.
     */
    public UserEquity grantToUser(Requester requester, Equity equity, UserId userId, BigDecimal quantity) {
        List<UserEquity> userEquities = userEquityRepository.userEquities(userId);
        for (UserEquity userEquity : userEquities) {
            if (userEquity.couldRenewal(equity)) {
                userEquity.renewal(equity, quantity);
                return userEquity;
            }
        }

        LocalDateTime effectiveTime = equity.effectiveTime(LocalDateTime.now());
        return new UserEquity(
                new UserEquityId(IdentityGenerator.nextIdentity()),
                userId,
                equity.equityId(),
                equity.name(),
                quantity,
                equity.unit(),
                effectiveTime,
                equity.expiresTime(effectiveTime, quantity),
                requester
        );
    }
}
