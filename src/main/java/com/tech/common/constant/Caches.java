package com.tech.common.constant;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * 本地缓存注册表。
 *
 * @author user
 * @since 2026-06-04
 */
public final class Caches {

    public static final String CACHE_USER = "userCache";

    private static final CacheSpec DEFAULT_SPEC = new CacheSpec(1_000, Duration.ofMinutes(5));
    private static final Map<String, CacheSpec> SPECS = Map.of(
            CACHE_USER, new CacheSpec(5_000, Duration.ofHours(1))
    );

    private Caches() {
    }

    public static Set<String> names() {
        return SPECS.keySet();
    }

    public static boolean contains(String cacheName) {
        return SPECS.containsKey(cacheName);
    }

    public static CacheSpec get(String cacheName) {
        CacheSpec spec = SPECS.get(cacheName);
        if (spec == null) {
            throw new IllegalArgumentException("未注册的缓存名称：" + cacheName);
        }
        return spec;
    }

    public static CacheSpec defaultSpec() {
        return DEFAULT_SPEC;
    }

    public record CacheSpec(long maximumSize, Duration expireAfterWrite) {
    }
}
