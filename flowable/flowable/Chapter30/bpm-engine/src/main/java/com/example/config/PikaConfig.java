package com.example.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class PikaConfig {
    @Value("${pika.host}")
    private String pickHost;
    @Value("${pika.port}")
    private int pickPort;
    @Value("${pika.timeout:1000ms}")
    private Duration readTimeout;
    @Value("${pika.connect-timeout:1000ms}")
    private Duration connectTimeout;
    @Value("${pika.jedis.pool.max-active:8}")
    private int maxActive;
    @Value("${pika.jedis.pool.max-idle:4}")
    private int minIdle;
    @Value("${pika.jedis.pool.min-idle:0}")
    private int maxIdle;
    @Value("${pika.jedis.pool.max-wait:1000ms}")
    private Duration maxWait;

    @Bean(name = "pikaTemplate")
    public RedisTemplate<String, String> pikaTemplate() {
        //配置服务器信息
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(pickHost, pickPort);
        //配置客户端连接池信息
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxWait(maxWait);
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration
                .builder()
                .readTimeout(readTimeout)
                .connectTimeout(connectTimeout)
                .usePooling()
                .poolConfig(poolConfig)
                .build();
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        //配置序列化方式
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
