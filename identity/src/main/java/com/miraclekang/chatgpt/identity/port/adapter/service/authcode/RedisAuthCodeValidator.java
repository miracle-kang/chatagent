package com.miraclekang.chatgpt.identity.port.adapter.service.authcode;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisAuthCodeValidator implements AuthCodeValidator {

    private static final String AUTH_CODE_MAP_KEY = "auth.authCodeMap";

    private final RedissonClient redissonClient;

    public RedisAuthCodeValidator(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;

        log.info(">>>>>> AuthCode validator initialized with RedissonClient <<<<<<");
    }

    @Override
    public String generateAuthCode(String identity, int expiresMinutes) {
        String authCode = RandomStringUtils.randomNumeric(6);
        redissonClient.getMapCache(AUTH_CODE_MAP_KEY, StringCodec.INSTANCE)
                .put(identity, authCode, expiresMinutes, TimeUnit.MINUTES);
        return authCode;
    }

    @Override
    public boolean validateAuthCode(String identity, String authCode) {
        RMapCache<String, String> cache = redissonClient.getMapCache(AUTH_CODE_MAP_KEY, StringCodec.INSTANCE);

        String cacheAuthCode = cache.get(identity);
        if (StringUtils.isBlank(cacheAuthCode)) {
            return false;
        }
        if (cacheAuthCode.equals(authCode)) {
            cache.remove(identity);
            return true;
        }
        return false;
    }
}
