package com.miraclekang.chatgpt.subscription.port.adapter.restapi.internal;

import com.miraclekang.chatgpt.subscription.application.equity.UserEquityInfoService;
import com.miraclekang.chatgpt.subscription.application.equity.querystack.UserEquityInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/internal/equity")
@Tag(name = "User equity info APIs", description = "User equity info APIs")
public class UserEquityInfoController {

    private final UserEquityInfoService userEquityInfoService;

    public UserEquityInfoController(UserEquityInfoService userEquityInfoService) {
        this.userEquityInfoService = userEquityInfoService;
    }

    @GetMapping("/user-equities")
    @Operation(summary = "Get user equities", description = "Get user equities")
    public Flux<UserEquityInfoDTO> getUserEquities(@RequestParam String userId) {
        return userEquityInfoService.userEquities(userId);
    }
}
