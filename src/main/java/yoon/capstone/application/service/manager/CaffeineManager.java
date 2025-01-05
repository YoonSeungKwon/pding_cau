package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class CaffeineManager implements CacheManager{

    private final ConcurrentHashMap<String, ReentrantLock> cacheLock = new ConcurrentHashMap<>();

    private final CaffeineCacheManager cacheManager;

    @Override
    public <T> void cachePut(String cacheName, String key, T value) {
        Cache cache = cacheManager.getCache(cacheName);
        cache.put(key, value);
    }

    @Override
    public <T> T cacheGet(String cacheName, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache==null)
            return null;
        return cache.get(key, type);
    }

    @Override
    public boolean available(String key) {
        if(!cacheLock.contains(key))
            cacheLock.put(key, new ReentrantLock());
        return cacheLock.get(key).tryLock();
    }

    @Override
    public boolean checkLock(String key){
        return cacheLock.get(key).isHeldByCurrentThread();
    }

    @Override
    public void unlock(String key){
        cacheLock.get(key).unlock();
    }
}
