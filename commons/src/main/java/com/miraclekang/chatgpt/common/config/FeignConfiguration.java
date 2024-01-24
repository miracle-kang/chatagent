package com.miraclekang.chatgpt.common.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;

@Slf4j
@Configuration
@EnableFeignClients(basePackages = "com.miraclekang.chatgpt.common.facade")
public class FeignConfiguration implements RequestInterceptor {

    public FeignConfiguration() {
        log.info("FeignConfiguration init");
    }

    @Override
    public void apply(RequestTemplate template) {

    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Decoder decoder(ObjectMapper objectMapper) {
        return new OptionalDecoder(
                new ResponseEntityDecoder((response, type) -> {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] resultByte;
                    try {
                        IOUtils.copy(response.body().asInputStream(), outputStream);
                    } catch (IOException e) {
                        throw new DecodeException(response.status(), "Read input stream error", response.request(), e);
                    }
                    resultByte = outputStream.toByteArray();

                    if (type instanceof Class || type instanceof ParameterizedType) {
                        JavaType javaType = objectMapper.constructType(type);
                        try {
                            JsonNode dataNode = objectMapper.readTree(resultByte);
                            if (dataNode.has("data") && dataNode.has("code")) {
                                dataNode = dataNode.get("data");
                            }
                            return objectMapper.convertValue(dataNode, javaType);
                        } catch (IOException e) {
                            throw new DecodeException(response.status(), "Error decode response body: "
                                    + new String(resultByte), response.request(), e);
                        }
                    }
                    throw new DecodeException(response.status(),
                            String.format("type is not an instance of Class or ParameterizedType: %s," +
                                    " response body: %s", type, new String(resultByte)),
                            response.request());
                }));
    }
}
