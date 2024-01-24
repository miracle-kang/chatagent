package com.miraclekang.chatgpt.identity.port.adapter.service.authcode;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn("redissonConfig")
public class AuthCodeValidatorConfiguration {

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public RedisAuthCodeValidator redisAuthCodeValidator(RedissonClient redissonClient) {
        return new RedisAuthCodeValidator(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean(AuthCodeValidator.class)
    public InMemoryAuthCodeValidator inMemoryAuthCodeValidator() {
        return new InMemoryAuthCodeValidator();
    }
}
