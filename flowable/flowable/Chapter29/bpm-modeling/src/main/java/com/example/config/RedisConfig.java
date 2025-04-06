package com.example.config;

import com.example.serializer.KryoRedisSerializer;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean(name = "processDefinitionCacheRedisTemplate")
    public RedisTemplate<String, ProcessDefinitionCacheEntry> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, ProcessDefinitionCacheEntry> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new KryoRedisSerializer<>());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new KryoRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
