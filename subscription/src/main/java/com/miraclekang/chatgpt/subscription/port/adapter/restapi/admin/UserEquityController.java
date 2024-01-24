package com.miraclekang.chatgpt.subscription.port.adapter.restapi.admin;

import com.miraclekang.chatgpt.common.restapi.SearchCriteriaParam;
import com.miraclekang.chatgpt.subscription.application.equity.UserEquityService;
import com.miraclekang.chatgpt.subscription.application.equity.querystack.UserEquityDTO;
import com.miraclekang.chatgpt.subscription.port.adapter.restapi.admin.dto.RenewalUserEquityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/equity/user-equities")
@Tag(name = "User equity APIs", description = "User equity APIs")
public class UserEquityController {

    private final UserEquityService userEquityService;

    public UserEquityController(UserEquityService userEquityService) {
        this.userEquityService = userEquityService;
    }

    @GetMapping("/{userEquityId}")
    @Operation(summary = "Get user equity", description = "Get a user equity")
    public Mono<UserEquityDTO> getUserEquity(@PathVariable String userEquityId) {
        return userEquityService.getUserEquity(userEquityId);
    }

    @PutMapping("/{userEquityId}/renewal")
    @Operation(summary = "Renewal user equity", description = "Renewal user equity")
    public Mono<UserEquityDTO> renewalUserEquity(@PathVariable String userEquityId,
                                                 @Valid @RequestBody RenewalUserEquityRequest request) {
        return userEquityService.renewalUserEquity(userEquityId, request.getRenewalQty());
    }

    @DeleteMapping("/{userEquityId}")
    @Operation(summary = "Remove user equity", description = "Remove a user equity")
    public Mono<Void> removeUserEquity(@PathVariable String userEquityId) {
        return userEquityService.removeUserEquity(userEquityId);
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Query user equities", description = "Query user equities")
    public Mono<Page<UserEquityDTO>> queryUserEquities(@ParameterObject SearchCriteriaParam criteriaParam,
                                                       @ParameterObject Pageable pageable) {
        return userEquityService.queryUserEquities(criteriaParam.toCriteria(), pageable);
    }
}
