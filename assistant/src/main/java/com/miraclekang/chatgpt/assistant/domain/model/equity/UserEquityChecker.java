package com.miraclekang.chatgpt.assistant.domain.model.equity;

import com.miraclekang.chatgpt.assistant.domain.model.billing.Token;
import com.miraclekang.chatgpt.assistant.domain.model.billing.TokenAccount;
import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.common.reactive.Requester;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

public class UserEquityChecker {

    private static final ChatModel DEFAULT_MODEL = ChatModel.GPT3_5;
    private static final Integer DEFAULT_MAX_TOKEN = 1024;

    private final Set<ChatModel> availableModels = Set.of(
            ChatModel.GPT3_5,
            ChatModel.GPT4
    );

    private final Requester requester;
    private final List<UserEquityInfo> userEquities;
    private final TokenAccount tokenAccount;

    UserEquityChecker(List<UserEquityInfo> userEquities,
                      TokenAccount tokenAccount,
                      Requester requester) {
        this.requester = requester;
        this.userEquities = userEquities;
        this.tokenAccount = tokenAccount;
    }

    /**
     * Check if the requester could use the model
     *
     * @param model model to use
     * @return true if the requester could use the model
     */
    public boolean allowUseModel(ChatModel model) {
        if (!availableModels.contains(model)) {
            return false;
        }
        if (requester.isAdmin()) {
            return true;
        }

        if (userEquities.isEmpty()) {
            return model == DEFAULT_MODEL;
        }

        // Check if the requester could use model with max token
        return userEquities.stream()
                .filter(UserEquityInfo::available)
                .anyMatch(userEquity -> userEquity.allowUseModel(model));
    }

    /**
     * Check if the requester could use the model with max token
     *
     * @param model           model to use
     * @param requestMaxToken max token requested
     */
    public Integer allowedMaxToken(ChatModel model, Integer requestMaxToken) {
        if (requester.isAdmin()) {
            return Math.min(model.getMaxTokens(), requestMaxToken);
        }

        if (userEquities.isEmpty()) {
            return Math.min(DEFAULT_MAX_TOKEN, requestMaxToken);
        }

        // Check if the requester could use model with max token
        Integer allowedMaxToken = userEquities.stream()
                .filter(UserEquityInfo::available)
                .map(userEquity -> userEquity.allowedMaxToken(model))
                .reduce(Integer::min)
                .orElse(0);
        return Math.min(allowedMaxToken, requestMaxToken);
    }

    /**
     * Check if the requester could use the token
     *
     * @param tokens tokens to use
     * @return true if the requester could use the token
     */
    public boolean couldUseToken(ChatModel model, List<Token> tokens) {
        return couldUseToken(model, tokens.stream().reduce(Token::add).orElse(Token.ZERO));
    }

    public boolean couldUseToken(ChatModel model, Token token) {
        // Administrator is always allowed
        if (requester.isAdmin()) {
            return true;
        }

        if (userEquities.isEmpty()) {
            return tokenAccount != null && tokenAccount.availableTokens() > token.numTokens();
        }

        return userEquities.stream()
                .filter(UserEquityInfo::available)
                .anyMatch(userEquity -> userEquity.couldUseToken(model, token, tokenAccount));
    }

    /**
     * Check if the requester need to pay the tokens for the model
     *
     * @param model model to use
     * @return true if the requester need to pay for the model
     */
    public boolean needToPay(ChatModel model, Token token, TokenAccount account) {
        // Administrator is always free
        if (requester.isAdmin()) {
            return false;
        }

        if (userEquities.isEmpty()) {
            return true;
        }

        // Check if the requester used token need to pay
        return userEquities.stream()
                .filter(UserEquityInfo::available)
                .allMatch(userEquity -> userEquity.needToPay(model, token, account));
    }
}
