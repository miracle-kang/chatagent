package com.miraclekang.chatgpt.identity.port.adapter.restapi.open;

import com.miraclekang.chatgpt.common.restapi.NoBodyWarp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@PreAuthorize("permitAll()")
@RequestMapping("/api/open/oauth2")
@Tag(name = "OAuth2 APIs", description = "OAuth2 APIs")
public class OAuth2Controller {

    @NoBodyWarp
    @Operation(summary = "Github Authorization", description = "Github Authorization")
    @GetMapping("/authorization/github")
    public Mono<Void> githubAuthorization() {
        return Mono.empty();
    }

    @NoBodyWarp
    @Operation(summary = "Github Login", description = "Github Login")
    @GetMapping("/login/github")
    public Mono<String> githubLogin() {
        return Mono.empty();
    }


    @NoBodyWarp
    @Operation(summary = "Google Authorization", description = "Google Authorization")
    @GetMapping("/authorization/google")
    public Mono<Void> googleAuthorization() {
        return Mono.empty();
    }

    @NoBodyWarp
    @Operation(summary = "Google Login", description = "Google Login")
    @GetMapping("/login/google")
    public Mono<String> googleLogin() {
        return Mono.empty();
    }
}
