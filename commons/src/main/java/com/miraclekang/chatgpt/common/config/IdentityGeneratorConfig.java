package com.miraclekang.chatgpt.common.config;

import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityGeneratorConfig {

    @Bean
    @ConditionalOnProperty(value = "snowflake.enable", havingValue = "true")
    public IdentityGenerator.Generator snowflakeIdentityGenerator(@Value("${snowflake.node-id:100}") Long nodeId) {
        return new IdentityGenerator.SnowflakeIdentityGenerator(nodeId);
    }

    @Bean
    @ConditionalOnMissingBean(IdentityGenerator.Generator.class)
    public IdentityGenerator.Generator defaultIdentityGenerator() {
        return new IdentityGenerator.DefaultIdentityGenerator();
    }
}
