package com.miraclekang.chatgpt.identity.domain.model.equity;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import reactor.core.publisher.Flux;

import java.util.List;

public interface UserEquityInfoService {

    Flux<UserEquityInfo> userEquities(UserId userId);
}
