package com.miraclekang.chatgpt.agent.domain.model.equity;

import com.miraclekang.chatgpt.agent.domain.model.identity.UserId;
import reactor.core.publisher.Flux;

public interface UserEquityInfoService {

    Flux<UserEquityInfo> userEquities(UserId userId);
}
