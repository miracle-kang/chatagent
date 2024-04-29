package com.miraclekang.chatgpt.agent.port.adapter.enviroment;

import com.miraclekang.chatgpt.agent.port.adapter.enviroment.properties.RedisProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    @ConditionalOnBean(RedisProperties.class)
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(properties.getAddress())
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword());

        return Redisson.create(config);
    }
}
