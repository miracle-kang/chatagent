package com.miraclekang.chatgpt.common.facade;

import com.miraclekang.chatgpt.common.facade.dto.UserEquityInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "subscription-service", url = "${remote.subscription.url:http://subscription}")
public interface SubscriptionServiceFacade {

    @GetMapping("/api/internal/equity/user-equities")
    List<UserEquityInfoDTO> getUserEquities(@RequestParam String userId);
}
