package com.miraclekang.chatgpt.common.config;

import com.miraclekang.chatgpt.common.access.TokenService;
import com.miraclekang.chatgpt.common.access.UserAuthentication;
import com.miraclekang.chatgpt.common.access.UserPrincipal;
import com.miraclekang.chatgpt.common.exception.ApplicationException;
import com.miraclekang.chatgpt.common.global.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
@ConditionalOnBean(TokenService.class)
@ConditionalOnClass({ServerHttpSecurity.class, EnableWebFluxSecurity.class})
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final TokenService tokenService;

    public SecurityConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/api/admin/**").hasRole("Administrator")
                        .anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.error(new ApplicationException(ex, ExceptionCode.Unauthorized)))
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.error(new ApplicationException(denied, ExceptionCode.Forbidden))))
                .build();
    }

    @Bean
    WebClientReactiveAuthorizationCodeTokenResponseClient authorizationCodeTokenResponseClient() {
        var client = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        client.setWebClient(defaultWebClient());
        return client;
    }

    @Bean
    DefaultReactiveOAuth2UserService reactiveOAuth2UserService() {
        var userService = new DefaultReactiveOAuth2UserService();
        userService.setWebClient(defaultWebClient());
        return userService;
    }

    @Bean
    OidcReactiveOAuth2UserService oidcReactiveOAuth2UserService() {
        var userService = new OidcReactiveOAuth2UserService();
        userService.setOauth2UserService(reactiveOAuth2UserService());
        return userService;
    }

    @Bean
    public WebClient defaultWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().proxyWithSystemProperties()))
                .build();
    }

    private ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            if (authentication.getPrincipal() != null) {
                authentication.setAuthenticated(true);
                return Mono.just(authentication);
            }
            return Mono.error(new ApplicationException(ExceptionCode.Unauthorized));
        };
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter(authenticationManager());
        webFilter.setServerAuthenticationConverter(exchange -> Mono.just(exchange.getRequest())
                .mapNotNull(request -> request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .map(this::authenticate));
        return webFilter;
    }

    /**
     * 验证用户
     */
    private UserAuthentication authenticate(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token is null");
        }
        UserPrincipal userPrincipal = tokenService.decodeToken(token);
        if (userPrincipal == null) {
            return new UserAuthentication(token, null, null);
        }
        return new UserAuthentication(token, userPrincipal, null);
    }
}
