package yoon.capstone.application.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public CaffeineCacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(cacheConfig());
        return cacheManager;
    }

    private Caffeine<Object, Object> cacheConfig(){
        return Caffeine.newBuilder()
                .expireAfterWrite(10*60L, TimeUnit.SECONDS)
                .maximumSize(10000);
    }


}
