//package yoon.capstone.application.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import yoon.capstone.application.service.domain.Projects;
//
//import java.time.Duration;

//@Configuration
//@Profile("redis-enable")
//@EnableCaching
//public class RedisConfig {
//
//    @Value("${REDIS_URL}")
//    private String redisUrl;
//    @Value("${REDIS_PORT}")
//    private String redisPort;
//    @Bean
//    @ConditionalOnBean(RedisCacheManager.class)
//    public CacheManager cacheManager(RedisConnectionFactory connectionFactory){
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
//                .entryTtl(Duration.ofMinutes(10L));
//
//        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory)
//                .cacheDefaults(redisCacheConfiguration).build();
//    }
//
//    @Bean
//    @ConditionalOnBean(RedisCacheManager.class)
//    public RedissonClient redissonClient(){
//        Config config = new Config();
//        config.useSingleServer()
//                .setAddress("redis://"+redisUrl+":"+redisPort);
//
//        return Redisson.create(config);
//    }
//
//    private RedisCacheConfiguration redisCacheConfiguration(){
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        Jackson2JsonRedisSerializer<Projects> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Projects.class);
//
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
//                .entryTtl(Duration.ofMinutes(10));
//    }
//
//
//}
