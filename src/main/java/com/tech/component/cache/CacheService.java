package com.tech.component.cache;

import com.tech.common.constant.Caches;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    public void clearCache(String cacheName) {
        getCache(cacheName).clear();
    }

    public void evictCache(String cacheName, Object key) {
        getCache(cacheName).evict(key);
    }

    private Cache getCache(String cacheName) {
        if (!Caches.contains(cacheName)) {
            throw new IllegalArgumentException("未注册的缓存名称：" + cacheName);
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("缓存未初始化：" + cacheName);
        }
        return cache;
    }
}
