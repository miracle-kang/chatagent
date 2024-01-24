package com.miraclekang.chatgpt.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miraclekang.chatgpt.common.access.TokenService;
import com.miraclekang.chatgpt.common.global.GlobalErrorHandler;
import com.miraclekang.chatgpt.common.global.GlobalResponseHandler;
import com.miraclekang.chatgpt.common.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;

@Configuration
public class GlobalConfig {

    @Bean
    @Order(99)
    @ConditionalOnMissingBean(GlobalResponseHandler.class)
    public GlobalResponseHandler globalResponseHandler(ServerCodecConfigurer serverCodecConfigurer,
                                                       RequestedContentTypeResolver contentTypeResolver,
                                                       ReactiveAdapterRegistry adapterRegistry,
                                                       ObjectMapper objectMapper) {
        return new GlobalResponseHandler(serverCodecConfigurer, contentTypeResolver, adapterRegistry, objectMapper);
    }

    @Bean
    @Order(-1)
    @ConditionalOnMissingBean(GlobalErrorHandler.class)
    public GlobalErrorHandler globalErrorHandler(DefaultErrorAttributes defaultErrorAttributes,
                                                 WebProperties webProperties,
                                                 ApplicationContext applicationContext,
                                                 ServerCodecConfigurer serverCodecConfigurer,
                                                 MessageSource messageSource) {
        return new GlobalErrorHandler(defaultErrorAttributes, webProperties,
                applicationContext, serverCodecConfigurer, messageSource);
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService(@Value("${auth.jwt.issuer}") String issuer,
                                     @Value("${auth.jwt.key}") String key,
                                     @Value("${auth.jwt.expires-hours}") Long expiresHours) {
        return new JwtTokenService(issuer, key, expiresHours);
    }
}
