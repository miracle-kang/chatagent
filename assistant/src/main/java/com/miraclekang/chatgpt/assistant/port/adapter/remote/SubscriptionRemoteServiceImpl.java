package com.miraclekang.chatgpt.assistant.port.adapter.remote;

import com.miraclekang.chatgpt.assistant.domain.model.chat.ChatModel;
import com.miraclekang.chatgpt.assistant.domain.model.equity.*;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.facade.SubscriptionServiceFacade;
import com.miraclekang.chatgpt.common.facade.dto.UserEquityInfoDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionRemoteServiceImpl implements UserEquityInfoService {

    private final SubscriptionServiceFacade subscriptionServiceFacade;

    public SubscriptionRemoteServiceImpl(SubscriptionServiceFacade subscriptionServiceFacade) {
        this.subscriptionServiceFacade = subscriptionServiceFacade;
    }

    @Override
    public List<UserEquityInfo> userEquities(UserId userId) {
        List<UserEquityInfoDTO> userEquities = subscriptionServiceFacade.getUserEquities(userId.getId());
        return userEquities.stream().map(equityInfoDTO -> new UserEquityInfo(
                new UserEquityId(equityInfoDTO.getUserEquityId()),
                new EquityId(equityInfoDTO.getEquityType(), equityInfoDTO.getEquityId()),
                equityInfoDTO.getEquityName(),
                equityInfoDTO.getQuantity(),
                equityInfoDTO.getUnit(),
                equityInfoDTO.getEffectiveTime() == null ? null : equityInfoDTO.getEffectiveTime().toLocalDateTime(),
                equityInfoDTO.getExpiresTime() == null ? null : equityInfoDTO.getExpiresTime().toLocalDateTime(),
                UserEquityStatus.valueOf(equityInfoDTO.getStatus()),
                new EquityLimitation(
                        equityInfoDTO.getLimitation().getEffective(),
                        equityInfoDTO.getLimitation().getMaxTokensPerMonth(),
                        equityInfoDTO.getLimitation().getMaxTokensPerDay(),
                        equityInfoDTO.getLimitation().getMaxTokensPerRequest(),
                        equityInfoDTO.getLimitation().getChatModels().stream().map(ChatModel::valueOf).toList()
                )
        )).toList();
    }
}
