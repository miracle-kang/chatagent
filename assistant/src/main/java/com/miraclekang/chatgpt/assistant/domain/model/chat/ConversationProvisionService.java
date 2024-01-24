package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.assistant.domain.model.equity.UserEquityCheckerProvider;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConversationProvisionService {

    // System conversation config
    private final UserEquityCheckerProvider equityCheckerProvision;

    public ConversationProvisionService(UserEquityCheckerProvider equityCheckerProvision) {
        this.equityCheckerProvision = equityCheckerProvision;
    }

    public Conversation provisionConversation(Requester requester, String name, ChatModel model, Boolean sendHistory,
                                              Double temperature, Double topP, Integer maxTokens) {
        return new Conversation(
                new ConversationId(IdentityGenerator.nextIdentity()),
                new UserId(requester.getUserId()),
                name,
                model,
                sendHistory,
                temperature,
                topP,
                maxTokens,
                null,
                null,
                List.of(),
                null,
                null,
                null,
                Map.of(),
                equityCheckerProvision.provision(requester, model)
        );
    }
}
