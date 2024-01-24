package com.miraclekang.chatgpt.identity.port.adapter.enviroment;

import com.miraclekang.chatgpt.identity.application.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;

    public OAuth2AuthenticationSuccessHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info(">>>>>> OAuth Login Success: {}", authentication.getPrincipal());
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
            return authenticationService.oauth2Authenticate(authenticationToken.getAuthorizedClientRegistrationId(),
                            authenticationToken.getPrincipal())
                    .switchIfEmpty(Mono.error(new RuntimeException("OAuth2 Login Failed")))
                    .flatMap(token -> {
                        webFilterExchange.getExchange().getResponse()
                                .getHeaders()
                                .setBearerAuth(token.getToken());
                        return Mono.empty();
                    });
        }

        return Mono.empty();
    }
}
