package yoon.capstone.application.service.manager;

public interface CacheManager {


    <T> void cachePut(String key , T value);

    <T> T cacheGet();

    boolean available(String key);

    boolean checkLock(String key);

    void unlock(String key);
}
