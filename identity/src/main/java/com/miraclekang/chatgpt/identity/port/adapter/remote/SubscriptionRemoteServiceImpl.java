package com.miraclekang.chatgpt.identity.port.adapter.remote;

import com.miraclekang.chatgpt.common.facade.SubscriptionServiceFacade;
import com.miraclekang.chatgpt.identity.domain.model.equity.*;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Component
public class SubscriptionRemoteServiceImpl implements UserEquityInfoService {

    private final SubscriptionServiceFacade subscriptionServiceFacade;

    public SubscriptionRemoteServiceImpl(SubscriptionServiceFacade subscriptionServiceFacade) {
        this.subscriptionServiceFacade = subscriptionServiceFacade;
    }

    @Override
    public Flux<UserEquityInfo> userEquities(UserId userId) {
        return Mono.just(userId.getId()).flatMap(uid -> blockingOperation(
                        () -> subscriptionServiceFacade.getUserEquities(uid)))
                .flatMapMany(Flux::fromIterable)
                .map(equityInfoDTO -> new UserEquityInfo(
                        new UserEquityId(equityInfoDTO.getUserEquityId()),
                        new EquityId(equityInfoDTO.getEquityType(), equityInfoDTO.getEquityId()),
                        equityInfoDTO.getEquityName(),
                        equityInfoDTO.getQuantity(),
                        equityInfoDTO.getUnit(),
                        equityInfoDTO.getEffectiveTime() == null ? null : equityInfoDTO.getEffectiveTime().toLocalDateTime(),
                        equityInfoDTO.getExpiresTime() == null ? null : equityInfoDTO.getExpiresTime().toLocalDateTime(),
                        UserEquityStatus.valueOf(equityInfoDTO.getStatus()),
                        new EquityLimitation(
                                equityInfoDTO.getLimitation().getEffective(),
                                equityInfoDTO.getLimitation().getMaxTokensPerMonth(),
                                equityInfoDTO.getLimitation().getMaxTokensPerDay(),
                                equityInfoDTO.getLimitation().getMaxTokensPerRequest(),
                                equityInfoDTO.getLimitation().getChatModels()
                        )
                ));
    }
}
