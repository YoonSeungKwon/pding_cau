package yoon.capstone.application.service.manager;

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
    public boolean checkLock(String key) {
        return false;
    }

    @Override
    public void unlock(String key) {

    }
}
