package com.miraclekang.chatgpt.subscription.port.adapter.restapi.admin;

import com.miraclekang.chatgpt.common.restapi.SearchCriteriaParam;
import com.miraclekang.chatgpt.subscription.application.equity.command.GrantToUserCommand;
import com.miraclekang.chatgpt.subscription.application.equity.querystack.UserEquityDTO;
import com.miraclekang.chatgpt.subscription.application.subscription.SubscriptionService;
import com.miraclekang.chatgpt.subscription.application.subscription.command.NewSubscriptionCommand;
import com.miraclekang.chatgpt.subscription.application.subscription.querystack.SubscriptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/equity/subscriptions")
@Tag(name = "Subscription APIs", description = "Subscription APIs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @Operation(summary = "Add subscription", description = "Add a new subscription")
    public Mono<SubscriptionDTO> addSubscription(@RequestBody NewSubscriptionCommand command) {
        return subscriptionService.addSubscription(command);
    }

    @PutMapping("/{subscriptionId}")
    @Operation(summary = "Update subscription", description = "Update a subscription")
    public Mono<SubscriptionDTO> updateSubscription(@PathVariable String subscriptionId,
                                                    @RequestBody NewSubscriptionCommand command) {
        return subscriptionService.updateSubscription(subscriptionId, command);
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription", description = "Get a subscription")
    public Mono<SubscriptionDTO> getSubscription(@PathVariable String subscriptionId) {
        return subscriptionService.getSubscription(subscriptionId);
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Query subscriptions", description = "Query subscriptions")
    public Mono<Page<SubscriptionDTO>> querySubscriptions(@ParameterObject SearchCriteriaParam criteriaParam,
                                                          @ParameterObject Pageable pageable) {
        return subscriptionService.querySubscriptions(criteriaParam.toCriteria(), pageable);
    }

    @PutMapping("/{subscriptionId}/grant")
    @Operation(summary = "Grant subscription to user", description = "Grant subscription to user")
    public Mono<UserEquityDTO> grantSubscriptionToUser(@PathVariable String subscriptionId,
                                                       @RequestBody GrantToUserCommand command) {
        return subscriptionService.grantSubscriptionToUser(subscriptionId, command);
    }
}
