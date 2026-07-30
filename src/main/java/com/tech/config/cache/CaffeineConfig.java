package com.tech.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tech.common.constant.Caches;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caffeine 本地缓存配置。
 *
 * @author Jonas
 * @version 1.0
 * @since 2025-09-28
 */
@EnableCaching
@Configuration
public class CaffeineConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager() {
            @NotNull
            @Override
            protected Cache<Object, Object> createNativeCaffeineCache(@NotNull String name) {
                Caches.CacheSpec spec = Caches.get(name);
                return Caffeine.newBuilder()
                        .maximumSize(spec.maximumSize())
                        .expireAfterWrite(spec.expireAfterWrite())
                        .build();
            }
        };
        cacheManager.setCacheNames(Caches.names());
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
