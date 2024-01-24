package com.miraclekang.chatgpt.identity.port.adapter.service.authcode;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryAuthCodeValidator implements AuthCodeValidator {

    private final SimpleLRUCache<String, String> authCodeMap;

    public InMemoryAuthCodeValidator() {
        this.authCodeMap = new SimpleLRUCache<>();

        log.info(">>>>>> AuthCode validator initialized with in-memory map <<<<<<");
    }

    @Override
    public String generateAuthCode(String identity, int expiresMinutes) {
        String authCode = RandomStringUtils.randomNumeric(6);
        authCodeMap.put(identity, authCode, Duration.ofMinutes(expiresMinutes));
        return authCode;
    }

    @Override
    public boolean validateAuthCode(String identity, String authCode) {
        if (!authCodeMap.containsKey(identity)) {
            return false;
        }
        boolean validate = authCodeMap.get(identity).equals(authCode);
        if (validate) {
            authCodeMap.remove(identity);
        }
        return validate;
    }

    public static class SimpleLRUCache<K, V> {

        /**
         * The default cache size
         */
        static final int DEFAULT_CACHE_SIZE = 1 << 16;  // aka 65535

        private final CacheMap<K, V> cacheMap;
        private final CacheMap<K, Long> entryExpiresMap;
        private final Map<K, ReentrantLock> resourceLocks = new HashMap<>();

        public SimpleLRUCache() {
            this(DEFAULT_CACHE_SIZE);
        }

        public SimpleLRUCache(int cacheSize) {
            this.cacheMap = new CacheMap<>(cacheSize);
            this.entryExpiresMap = new CacheMap<>(cacheSize);
        }

        public boolean put(K key, V value) {
            cacheMap.put(key, value);
            return true;
        }

        public boolean put(K key, V value, Duration expires) {
            entryExpiresMap.put(key, System.currentTimeMillis() + expires.toMillis());
            cacheMap.put(key, value);
            return true;
        }

        public void renewal(K key, Duration expires) {
            if (!entryExpiresMap.containsKey(key)) {
                return;
            }
            entryExpiresMap.put(key, System.currentTimeMillis() + expires.toMillis());
        }

        public V get(K key) {
            if (expired(key)) {
                return null;
            }
            return cacheMap.get(key);
        }

        public V remove(K key) {
            if (expired(key)) {
                return null;
            }
            return cacheMap.remove(key);
        }

        public boolean containsKey(K key) {
            if (expired(key)) {
                return false;
            }
            return cacheMap.containsKey(key);
        }

        private boolean expired(K key) {
            Long expires = entryExpiresMap.get(key);
            if (expires == null) {
                return false;
            }
            long current = System.currentTimeMillis();
            if (current > expires) {
                cacheMap.remove(key);
                entryExpiresMap.remove(key);
                return true;
            }
            return false;
        }

        public List<V> values() {
            return cacheMap.entrySet().stream()
                    .filter(entry -> {
                        Long expires = entryExpiresMap.get(entry.getKey());
                        if (expires != null) {
                            return System.currentTimeMillis() <= expires;
                        }
                        return true;
                    }).map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }

        public V cacheGet(K key, Supplier<V> resourceSupplier, Duration expiresDuration) {
            if (this.containsKey(key)) {
                return cacheMap.get(key);
            }

            ReentrantLock resourceLock;
            synchronized (this) {
                resourceLock = resourceLocks.computeIfAbsent(key, $ -> new ReentrantLock());
            }
            try {
                resourceLock.lock();
                if (this.containsKey(key)) {
                    return cacheMap.get(key);
                }
                V cachedData = resourceSupplier.get();
                this.put(key, cachedData, expiresDuration);
                return cachedData;
            } finally {
                resourceLock.unlock();
                resourceLocks.remove(key);
            }
        }

        public void clear() {
            cacheMap.clear();
            entryExpiresMap.clear();
        }

        static class CacheMap<K, V> extends LinkedHashMap<K, V> {

            /**
             * Cache size
             */
            private final int cacheSize;

            public CacheMap(int cacheSize) {
                super(cacheSize, 0.75f, true);
                this.cacheSize = cacheSize;
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > cacheSize;
            }
        }
    }
}
