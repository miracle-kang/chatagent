package com.miraclekang.chatgpt.subscription.application.equity;

import com.miraclekang.chatgpt.subscription.application.equity.querystack.UserEquityDTO;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityRepositoryDelegate;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquityId;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquityRepository;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquity_;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserEquityService {

    private final UserEquityRepository userEquityRepository;
    private final EquityRepositoryDelegate equityRepositoryDelegate;

    public UserEquityService(UserEquityRepository userEquityRepository,
                             EquityRepositoryDelegate equityRepositoryDelegate) {
        this.userEquityRepository = userEquityRepository;
        this.equityRepositoryDelegate = equityRepositoryDelegate;
    }


    // Query user equity
    public Mono<Page<UserEquityDTO>> queryUserEquities(List<SearchCriteria> searchCriteria, Pageable pageable) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(UserEquity_.ID).descending());

                    return userEquityRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest)
                            .map(UserEquityDTO::from);
                });
    }

    // Get user equity
    public Mono<UserEquityDTO> getUserEquity(String anUserEquityId) {
        return Mono.just(new UserEquityId(anUserEquityId))
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userEquityRepository::findByUserEquityId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User equity not found")))
                .map(UserEquityDTO::from);
    }

    public Mono<UserEquityDTO> renewalUserEquity(String anUserEquityId, Long renewalQty) {
        Validate.isTrue(renewalQty != null, "Renewal quantity is required");
        Validate.isTrue(renewalQty > 0, "Renewal quantity must be greater than 0");

        return Mono.just(new UserEquityId(anUserEquityId))
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userEquityRepository::findByUserEquityId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User equity not found")))
                .map(userEquity -> {
                    userEquity.renewal(equityRepositoryDelegate, BigDecimal.valueOf(renewalQty));
                    return userEquityRepository.save(userEquity);
                })
                .map(UserEquityDTO::from);
    }

    public Mono<Void> removeUserEquity(String anUserEquityId) {
        return Mono.just(new UserEquityId(anUserEquityId))
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userEquityRepository::findByUserEquityId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User equity not found")))
                .doOnNext(userEquityRepository::delete)
                .then();
    }
}
