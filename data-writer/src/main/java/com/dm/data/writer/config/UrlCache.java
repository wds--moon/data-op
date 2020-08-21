package com.dm.data.writer.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 手动实现进程基本的缓存
 * @author wendongshan
 */
@Configuration
public class UrlCache {
    @Bean
    public Cache<String, Integer> getCache() {
        /**
         * 缓存有效期2s
         */
        return CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.SECONDS).build();
    }
}
