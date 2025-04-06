package com.example.demo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collections;
import java.util.List;

public class RedisLockUtils {
    private static final Long SUCCESS = 1L;
    private static Logger log = LoggerFactory.getLogger(RedisLockUtils.class);

    //获取锁
    public static Boolean getDistributedLock(RedisTemplate<String, String> redisClient,
                                             String key, String value, int expireTime) {
        String script = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 "
                + "then "
                + "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) "
                + "else return 0 end "
                + "else return 0 end";
        return executeLockScript(redisClient, script, key, value, expireTime);
    }

    //释放锁
    public static boolean releaseDistributedLock(RedisTemplate<String, String> redisClient, String lockKey, String value) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] "
                + "then return redis.call('del', KEYS[1]) "
                + "else return 0 end";
        RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);

        Object result = redisClient.execute(redisScript,
                new StringRedisSerializer(),
                new StringRedisSerializer(),
                formatKey(lockKey),
                value);
        if (SUCCESS.equals(result)) {
            return true;
        }

        return false;
    }

    private static Boolean executeLockScript(RedisTemplate<String, String> redisTemplate,
                                             String script,
                                             String lockKey,
                                             String value,
                                             int expireTime) {
        Boolean ret = null;
        try {
            RedisScript<String> redisScript = new DefaultRedisScript(script, String.class);
            Object result = redisTemplate.execute(redisScript,
                    new StringRedisSerializer(),
                    new StringRedisSerializer(),
                    formatKey(lockKey),
                    value,
                    String.valueOf(expireTime));

            if (SUCCESS.equals(result)) {
                return true;
            }

        } catch (Exception e) {
            log.error("GetDistributedLock Exception ex", e);
        }
        return ret;
    }

    private static List<String> formatKey(String lockKey) {
        return Collections.singletonList(lockKey);
    }
}
