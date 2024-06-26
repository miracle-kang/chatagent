package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.miraclekang.chatgpt.agent.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.agent.domain.model.equity.UserEquityCheckerProvider;
import com.miraclekang.chatgpt.agent.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.reactive.Requester;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Slf4j
@Service
public class TokenChargeService {

    private final TokenAccountProvision accountProvision;
    private final UserEquityCheckerProvider equityCheckerProvider;

    private final TokenChargeRepository tokenChargeRepository;
    private final TokenAccountRepository tokenAccountRepository;

    public TokenChargeService(TokenAccountProvision accountProvision,
                              UserEquityCheckerProvider equityCheckerProvider,
                              TokenChargeRepository tokenChargeRepository,
                              TokenAccountRepository tokenAccountRepository) {
        this.accountProvision = accountProvision;
        this.equityCheckerProvider = equityCheckerProvider;
        this.tokenChargeRepository = tokenChargeRepository;
        this.tokenAccountRepository = tokenAccountRepository;
    }

    public Mono<TokenCharge> chargeToken(Requester requester, ChatModel model, Token token) {
        return Mono.just(new UserId(requester.getUserId()))
                .flatMap(userId -> {
                    TokenAccount tokenAccount = accountProvision.provision(userId, model);
                    return blockingOperation(() -> tokenAccountRepository.save(tokenAccount));
                })
                .flatMap(tokenAccount -> equityCheckerProvider.provision(requester, model)
                        .flatMap(equityChecker -> {
                            TokenCharge tokenCharge = tokenAccount.chargeToken(token, equityChecker);
                            log.debug("Charged {} tokens to account {} for model {}, need to pay? {}",
                                    token.numTokens(), tokenAccount.getAccountId(), model, tokenCharge.getPaid());
                            return blockingOperation(() -> tokenChargeRepository.save(tokenCharge));
                        }));
    }

    public Mono<TokenCharge> chargeTokens(Requester requester, ChatModel model, List<Token> tokens) {
        return chargeToken(requester, model, tokens.stream().reduce(Token::add).orElse(Token.ZERO));
    }
}
