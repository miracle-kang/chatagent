package com.miraclekang.chatgpt.common.reactive;

import com.miraclekang.chatgpt.common.access.Authentication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

public class Requester {

    public static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH,
            Locale.SIMPLIFIED_CHINESE,
            Locale.TRADITIONAL_CHINESE
    );
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    private final ServerWebExchange exchange;
    private final Authentication authentication;

    private final String requestId;
    private final String ip;
    private final Locale locale;

    public Requester(ServerWebExchange exchange, Authentication authentication,
                     String requestId, String ip, Locale locale) {
        this.exchange = exchange;
        this.authentication = authentication;
        this.requestId = requestId;
        this.ip = ip;
        this.locale = locale;
    }

    public ServerWebExchange getExchange() {
        return exchange;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getIp() {
        return ip;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isAuthenticated() {
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean isAnonymous() {
        return !isAuthenticated();
    }

    public String getName() {
        return authentication == null ? null : authentication.getName();
    }

    public boolean isAdmin() {
        return isAuthenticated() && StringUtils.equals(authentication.getUserType(), "Administrator");
    }

    public boolean isCustomer() {
        return isAuthenticated() && StringUtils.equals(authentication.getUserType(), "Customer");
    }

    public String getUserId() {
        return isAuthenticated() ? authentication.getUserId() : null;
    }

    public String getUsername() {
        return isAuthenticated() ? authentication.getUsername() : null;
    }

    public boolean isRequestUser(String userId) {
        return isAuthenticated() && authentication.getUserId().equals(userId);
    }

    public static Mono<Requester> currentRequester() {
        return Mono.deferContextual(Mono::just)
                .flatMap(contextView -> {
                    ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
                    return Mono.just(contextView)
                            .flatMap(context -> context.<Mono<SecurityContext>>getOrEmpty(SecurityContext.class)
                                    .orElseGet(Mono::empty))
                            .filter(context -> context.getAuthentication() instanceof Authentication)
                            .map(securityContext -> new Requester(
                                    exchange, (Authentication) securityContext.getAuthentication(),
                                    exchange.getRequest().getId(),
                                    getIpAddress(exchange.getRequest()),
                                    getLocale(exchange.getRequest())))
                            .switchIfEmpty(Mono.just(new Requester(
                                    exchange, null,
                                    exchange.getRequest().getId(),
                                    getIpAddress(exchange.getRequest()),
                                    getLocale(exchange.getRequest()))));
                });
    }

    public static Locale getLocale(ServerHttpRequest request) {
        Locale locale = Locale.lookup(request.getHeaders().getAcceptLanguage(), SUPPORTED_LOCALES);
        if (locale == null) {
            return DEFAULT_LOCALE;
        }
        return Locale.SIMPLIFIED_CHINESE;
    }

    public static String getIpAddress(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().getHostString();
        }
        return ip;
    }
}