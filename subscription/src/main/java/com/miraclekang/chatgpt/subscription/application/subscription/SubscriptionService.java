package com.miraclekang.chatgpt.subscription.application.subscription;

import com.miraclekang.chatgpt.subscription.application.equity.command.GrantToUserCommand;
import com.miraclekang.chatgpt.subscription.application.equity.querystack.UserEquityDTO;
import com.miraclekang.chatgpt.subscription.application.subscription.command.NewSubscriptionCommand;
import com.miraclekang.chatgpt.subscription.application.subscription.querystack.SubscriptionDTO;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import com.miraclekang.chatgpt.subscription.domain.model.equity.EquityGrantService;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquity;
import com.miraclekang.chatgpt.subscription.domain.model.equity.UserEquityRepository;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.Subscription;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionId;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.SubscriptionRepository;
import com.miraclekang.chatgpt.subscription.domain.model.equity.subscription.Subscription_;
import com.miraclekang.chatgpt.subscription.domain.model.identity.UserId;
import com.miraclekang.chatgpt.subscription.domain.model.identity.UserInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class SubscriptionService {

    private final UserInfoService userInfoService;
    private final EquityGrantService equityGrantService;

    private final SubscriptionRepository subscriptionRepository;
    private final UserEquityRepository userEquityRepository;

    public SubscriptionService(UserInfoService userInfoService,
                               EquityGrantService equityGrantService,
                               SubscriptionRepository subscriptionRepository,
                               UserEquityRepository userEquityRepository) {
        this.userInfoService = userInfoService;
        this.equityGrantService = equityGrantService;
        this.subscriptionRepository = subscriptionRepository;
        this.userEquityRepository = userEquityRepository;
    }

    // Add subscription
    @Transactional
    public Mono<SubscriptionDTO> addSubscription(NewSubscriptionCommand command) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    var subscription = new Subscription(
                            new SubscriptionId(IdentityGenerator.nextIdentity()),
                            command.getName(),
                            command.getLevel(),
                            command.getType(),
                            command.getDisabled(),
                            command.getMaxTokensPerMonth(),
                            command.getMaxTokensPerDay(),
                            command.getMaxTokensPerRequest(),
                            command.getChatModels(),
                            requester
                    );
                    return subscriptionRepository.save(subscription);
                })
                .map(SubscriptionDTO::from);
    }

    // Update subscription
    @Transactional
    public Mono<SubscriptionDTO> updateSubscription(String aSubscriptionId, NewSubscriptionCommand command) {
        SubscriptionId subscriptionId = new SubscriptionId(aSubscriptionId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(subscriptionId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(subscriptionRepository::findBySubscriptionId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Subscription not found")))
                        .map(subscription -> {
                            subscription.update(
                                    command.getName(),
                                    command.getLevel(),
                                    command.getType(),
                                    command.getDisabled(),
                                    command.getMaxTokensPerMonth(),
                                    command.getMaxTokensPerDay(),
                                    command.getMaxTokensPerRequest(),
                                    command.getChatModels(),
                                    requester
                            );
                            return subscriptionRepository.save(subscription);
                        }))
                .map(SubscriptionDTO::from);
    }

    // Delete subscription
    @Transactional
    public Mono<Void> deleteSubscription(String aSubscriptionId) {
        SubscriptionId subscriptionId = new SubscriptionId(aSubscriptionId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(subscriptionId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(subscriptionRepository::findBySubscriptionId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Subscription not found")))
                        .flatMap(subscription -> blockingOperation(() -> subscriptionRepository.delete(subscription))));
    }

    // Get subscription
    public Mono<SubscriptionDTO> getSubscription(String aSubscriptionId) {
        SubscriptionId subscriptionId = new SubscriptionId(aSubscriptionId);
        return Mono.just(subscriptionId)
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(subscriptionRepository::findBySubscriptionId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Subscription not found")))
                .map(SubscriptionDTO::from);
    }

    // Query subscriptions
    public Mono<Page<SubscriptionDTO>> querySubscriptions(List<SearchCriteria> searchCriteria, Pageable pageable) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Subscription_.ID).descending());

                    return subscriptionRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest)
                            .map(SubscriptionDTO::from);
                });
    }

    // Grant subscription to user
    @Transactional
    public Mono<UserEquityDTO> grantSubscriptionToUser(String aSubscriptionId, GrantToUserCommand command) {
        SubscriptionId subscriptionId = new SubscriptionId(aSubscriptionId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(subscriptionId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(subscriptionRepository::findBySubscriptionId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Subscription not found")))
                        .filter(subscription -> userInfoService.existsByUserId(new UserId(command.getUserId())))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                        .map(subscription -> {
                            UserEquity userEquity = equityGrantService.grantToUser(requester,
                                    subscription,
                                    new UserId(command.getUserId()),
                                    command.getQuantity());
                            return userEquityRepository.save(userEquity);
                        }).map(UserEquityDTO::from));
    }
}
