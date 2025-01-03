package yoon.capstone.application.service.manager;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CaffeineCacheManager implements CacheManager{

    private final ConcurrentHashMap<String, ReentrantLock> cacheLock = new ConcurrentHashMap<>();


    @Override
    public <T> void cachePut(String key, T value) {

    }

    @Override
    public <T> T cacheGet() {
        return null;
    }

    @Override
    public boolean available(String key) {
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
