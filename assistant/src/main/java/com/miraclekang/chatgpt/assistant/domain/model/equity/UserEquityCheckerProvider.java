package com.miraclekang.chatgpt.assistant.domain.model.equity;

import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccountProvision;
import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.reactive.Requester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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

    public Mono<UserEquityChecker> provision(Requester requester, ChatModel model) {
        return userEquityInfoService.userEquities(new UserId(requester.getUserId()))
                .collectList()
                .switchIfEmpty(Mono.just(List.of()))
                .map(userEquities ->
                        new UserEquityChecker(userEquities,
                                tokenAccountProvision.provision(new UserId(requester.getUserId()), model),
                                requester
                        )
                );
    }
}
