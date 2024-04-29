package com.miraclekang.chatgpt.assistant.domain.model.equity;

import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import reactor.core.publisher.Flux;

public interface UserEquityInfoService {

    Flux<UserEquityInfo> userEquities(UserId userId);
}
