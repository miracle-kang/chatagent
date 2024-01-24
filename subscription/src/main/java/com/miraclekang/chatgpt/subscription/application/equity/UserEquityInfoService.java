package com.miraclekang.chatgpt.subscription.application.equity;

import com.miraclekang.chatgpt.subscription.application.equity.querystack.UserEquityInfoDTO;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityRepositoryDelegate;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquityRepository;
import com.miraclekang.chatgpt.subscription.domain.model.identity.UserId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class UserEquityInfoService {

    private final UserEquityRepository userEquityRepository;
    private final EquityRepositoryDelegate equityRepositoryDelegate;

    public UserEquityInfoService(UserEquityRepository userEquityRepository, EquityRepositoryDelegate equityRepositoryDelegate) {
        this.userEquityRepository = userEquityRepository;
        this.equityRepositoryDelegate = equityRepositoryDelegate;
    }

    public Flux<UserEquityInfoDTO> userEquities(String userId) {
        return blockingOperation(() -> userEquityRepository.userEquities(new UserId(userId)))
                .flatMapMany(Flux::fromIterable)
                .map(userEquity -> UserEquityInfoDTO.from(userEquity.equityInfo(equityRepositoryDelegate)));
    }
}
