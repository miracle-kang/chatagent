package com.miraclekang.chatgpt.assistant.application.chat;

import com.miraclekang.chatgpt.assistant.application.chat.querystack.TokenAccountDTO;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccount;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccountId;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccountRepository;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccount_;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserInfoService;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class TokenAccountService {

    private final UserInfoService userInfoService;
    private final TokenAccountRepository tokenAccountRepository;

    public TokenAccountService(UserInfoService userInfoService,
                               TokenAccountRepository tokenAccountRepository) {
        this.userInfoService = userInfoService;
        this.tokenAccountRepository = tokenAccountRepository;
    }

    public Mono<Page<TokenAccountDTO>> queryTokenAccounts(List<SearchCriteria> searchCriteria, Pageable pageable) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(TokenAccount_.LATEST_TOKEN_USED_TIME).descending());

                    return tokenAccountRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest)
                            .map(this::toDTO);
                });
    }

    public Mono<TokenAccountDTO> getTokenAccount(String anAccountId) {
        return Mono.just(new TokenAccountId(anAccountId))
                .mapNotNull(tokenAccountRepository::findByAccountId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token account not found")))
                .map(this::toDTO);
    }

    public TokenAccountDTO toDTO(TokenAccount tokenAccount) {
        String username = userInfoService.username(tokenAccount.getOwnerUserId());
        return new TokenAccountDTO(
                tokenAccount.getAccountId().getId(),
                tokenAccount.getOwnerUserId().toString(),
                username,
                tokenAccount.getModel(),
                tokenAccount.getTotalTokens(),
                tokenAccount.getPaidTokens(),
                tokenAccount.getLatestTokenUsedTime(),
                tokenAccount.tokenUsage()
        );
    }
}
