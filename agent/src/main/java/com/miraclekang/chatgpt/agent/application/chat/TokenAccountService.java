package com.miraclekang.chatgpt.agent.application.chat;

import com.miraclekang.chatgpt.agent.application.chat.querystack.TokenAccountDTO;
import com.miraclekang.chatgpt.agent.domain.model.billing.TokenAccount;
import com.miraclekang.chatgpt.agent.domain.model.billing.TokenAccountId;
import com.miraclekang.chatgpt.agent.domain.model.billing.TokenAccountRepository;
import com.miraclekang.chatgpt.agent.domain.model.billing.TokenAccount_;
import com.miraclekang.chatgpt.agent.domain.model.identity.UserInfoService;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
                .flatMap(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(TokenAccount_.LATEST_TOKEN_USED_TIME).descending());

                    Page<TokenAccount> page = tokenAccountRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest);

                    return Flux.fromIterable(page.getContent()).flatMap(this::toDTO)
                            .collectList()
                            .map(tokenAccountDTOs -> new PageImpl<>(tokenAccountDTOs, page.getPageable(), page.getTotalElements()));
                });
    }

    public Mono<TokenAccountDTO> getTokenAccount(String anAccountId) {
        return Mono.just(new TokenAccountId(anAccountId))
                .mapNotNull(tokenAccountRepository::findByAccountId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token account not found")))
                .flatMap(this::toDTO);
    }

    public Mono<TokenAccountDTO> toDTO(TokenAccount tokenAccount) {
        return userInfoService.username(tokenAccount.getOwnerUserId())
                .switchIfEmpty(Mono.just("Unknown"))
                .map(username -> new TokenAccountDTO(
                        tokenAccount.getAccountId().getId(),
                        tokenAccount.getOwnerUserId().toString(),
                        username,
                        tokenAccount.getModel(),
                        tokenAccount.getTotalTokens(),
                        tokenAccount.getPaidTokens(),
                        tokenAccount.getLatestTokenUsedTime(),
                        tokenAccount.tokenUsage()
                ));
    }
}
