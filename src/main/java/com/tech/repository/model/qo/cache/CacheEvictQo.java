package com.tech.repository.model.qo.cache;

import lombok.Data;

/**
 * CacheEvictQo
 *
 * @author Jonas
 * @version 1.0
 * @since 2025-09-29
 */
@Data
public class CacheEvictQo {
    /**
     * 缓存名称
     */
    private String cacheName;
    /**
     * 缓存Key
     */
    private Object cacheKey;
}
