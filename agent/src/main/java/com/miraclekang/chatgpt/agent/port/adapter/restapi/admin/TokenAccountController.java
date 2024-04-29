package com.miraclekang.chatgpt.agent.port.adapter.restapi.admin;

import com.miraclekang.chatgpt.agent.application.chat.TokenAccountService;
import com.miraclekang.chatgpt.agent.application.chat.querystack.TokenAccountDTO;
import com.miraclekang.chatgpt.common.restapi.SearchCriteriaParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/token-accounts")
@Tag(name = "Token account APIs", description = "Token account APIs")
public class TokenAccountController {

    private final TokenAccountService tokenAccountService;

    public TokenAccountController(TokenAccountService tokenAccountService) {
        this.tokenAccountService = tokenAccountService;
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Query token accounts", description = "Query token accounts")
    public Mono<Page<TokenAccountDTO>> queryTokenAccounts(@ParameterObject SearchCriteriaParam criteriaParam,
                                                          @ParameterObject Pageable pageable) {
        return tokenAccountService.queryTokenAccounts(criteriaParam.toCriteria(), pageable);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get a token account", description = "Get a token account")
    public Mono<TokenAccountDTO> getTokenAccount(@PathVariable String accountId) {
        return tokenAccountService.getTokenAccount(accountId);
    }

}
