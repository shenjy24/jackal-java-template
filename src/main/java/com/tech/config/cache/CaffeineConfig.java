package com.tech.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tech.common.constant.Caches;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * CaffeineConfig
 *
 * @author Jonas
 * @version 1.0
 * @since 2025-09-28
 */
@Slf4j
@EnableCaching
@Configuration
public class CaffeineConfig {

    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager() {
            @NotNull
            @Override
            protected Cache<Object, Object> createNativeCaffeineCache(@NotNull String name) {
                return switch (name) {
                    case Caches.CACHE_USER -> createUserCache();
                    default -> createDefaultCache(name);
                };
            }
        };
    }

    /**
     * 用户缓存：访问频繁、数据更新较少
     * 策略：大容量 + 较长过期时间
     */
    private Cache<Object, Object> createUserCache() {
        return Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(Duration.ofHours(1))
                .build();
    }

    /**
     * 默认缓存策略
     */
    private Cache<Object, Object> createDefaultCache(String cacheName) {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }
}
