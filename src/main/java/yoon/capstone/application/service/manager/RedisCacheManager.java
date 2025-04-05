package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("redis-enable")
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager{
    @Override
    public <T> void cachePut(String cacheName, String key, T value) {

    }

    @Override
    public <T> T cacheGet(String cacheName, String key, Class<T> type) {
        return null;
    }

    @Override
    public boolean available(String key) {
        return false;
    }

    @Override
    public boolean available(String key, long time) {
        return false;
    }

    @Override
    public boolean checkLock(String key) {
        return false;
    }

    @Override
    public void unlock(String key) {

    }
}
