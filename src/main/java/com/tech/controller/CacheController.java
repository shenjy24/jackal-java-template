package com.tech.controller;

import com.tech.model.qo.cache.CacheClearQo;
import com.tech.model.qo.cache.CacheEvictQo;
import com.tech.service.infra.cache.CacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author shenjy
 * @version 1.0
 * @since 2025-02-11
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/web/cache")
public class CacheController {

    private final CacheService cacheService;

    /**
     * 清空缓存
     */
    @RequestMapping("/clear")
    public void clearCache(@Valid @RequestBody CacheClearQo qo) {
        cacheService.clearCache(qo.getCacheName());
    }

    /**
     * 删除缓存
     */
    @RequestMapping("/evict")
    public void evictCache(@Valid @RequestBody CacheEvictQo qo) {
        cacheService.evictCache(qo.getCacheName(), qo.getCacheKey());
    }
}
