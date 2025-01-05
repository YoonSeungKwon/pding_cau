package yoon.capstone.application.service.manager;

public interface CacheManager {

    <T> void cachePut(String cacheName, String key, T value);

    <T> T cacheGet(String cacheName, String key, Class<T> type);

    boolean available(String key);

    boolean checkLock(String key);

    void unlock(String key);

}
