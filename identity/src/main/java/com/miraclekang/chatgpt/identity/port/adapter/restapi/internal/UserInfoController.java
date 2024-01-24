package com.miraclekang.chatgpt.identity.port.adapter.restapi.internal;

import com.miraclekang.chatgpt.identity.application.user.UserInfoService;
import com.miraclekang.chatgpt.identity.application.user.querystack.UserInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/internal/users")
@Tag(name = "User Info APIs", description = "User Info APIs")
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/{userId}/info")
    @Operation(summary = "Get user info", description = "Get user info")
    public Mono<UserInfoDTO> userInfo(@PathVariable String userId) {
        return userInfoService.userInfo(userId);
    }

    @GetMapping("/{userId}/username")
    @Operation(summary = "Get username", description = "Get username")
    public Mono<String> username(@PathVariable String userId) {
        return userInfoService.username(userId);
    }

    @GetMapping("/{userId}/exists")
    @Operation(summary = "Check user exists", description = "Check user exists")
    public Mono<Boolean> existsUser(@PathVariable String userId) {
        return userInfoService.existsUser(userId);
    }
}
