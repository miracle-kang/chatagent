package com.miraclekang.chatgpt.assistant.domain.model.equity;

import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccountProvision;
import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEquityCheckerProvider {

    private final UserEquityInfoService userEquityInfoService;
    private final TokenAccountProvision tokenAccountProvision;

    public UserEquityCheckerProvider(UserEquityInfoService userEquityInfoService,
                                     TokenAccountProvision tokenAccountProvision) {
        this.userEquityInfoService = userEquityInfoService;
        this.tokenAccountProvision = tokenAccountProvision;
    }

    public UserEquityChecker provision(Requester requester, ChatModel model) {

        List<UserEquityInfo> userEquityList = userEquityInfoService.userEquities(new UserId(requester.getUserId()));
        return new UserEquityChecker(
                userEquityList,
                tokenAccountProvision.provision(new UserId(requester.getUserId()), model),
                requester
        );
    }
}
