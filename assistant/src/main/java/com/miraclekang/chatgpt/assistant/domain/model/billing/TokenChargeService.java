package com.miraclekang.chatgpt.assistant.domain.model.billing;

import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.assistant.domain.model.equity.UserEquityCheckerProvider;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.reactive.Requester;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public TokenCharge chargeToken(Requester requester, ChatModel model, Token token) {
        TokenAccount tokenAccount = accountProvision.provision(new UserId(requester.getUserId()), model);

        TokenCharge tokenCharge = tokenAccount.chargeToken(token,
                equityCheckerProvider.provision(requester, model));

        tokenChargeRepository.save(tokenCharge);
        tokenAccountRepository.save(tokenAccount);

        log.debug("Charged {} tokens to account {} for model {}, need to pay? {}",
                token.numTokens(), tokenAccount.getAccountId(), model, tokenCharge.getPaid());
        return tokenCharge;
    }

    public TokenCharge chargeTokens(Requester requester, ChatModel model, List<Token> tokens) {
        return chargeToken(requester, model, tokens.stream().reduce(Token::add).orElse(Token.ZERO));
    }
}
