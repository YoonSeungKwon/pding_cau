package yoon.capstone.application.service.manager;

import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Profile("caffeine-enable")
@RequiredArgsConstructor
public class CaffeineCacheManager implements CacheManager{

    private final ConcurrentHashMap<String, ReentrantLock> cacheLock = new ConcurrentHashMap<>();

    private final org.springframework.cache.caffeine.CaffeineCacheManager cacheManager;

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
        cacheLock.computeIfAbsent(key, k -> new ReentrantLock());

        return cacheLock.get(key).tryLock();
    }

    @Override
    public boolean available(String key, long t){
        cacheLock.computeIfAbsent(key, k -> new ReentrantLock());

        try{
            return cacheLock.get(key).tryLock(t, TimeUnit.SECONDS);
        }catch (Exception e){
            return false;
        }
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
