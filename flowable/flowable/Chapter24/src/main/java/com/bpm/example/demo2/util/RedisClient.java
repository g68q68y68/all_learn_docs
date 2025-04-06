package com.bpm.example.demo2.util;

import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class RedisClient {

    @Setter
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     * @param key 可以传一个值或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                List<String> keys = (List<String>)CollectionUtils.arrayToList(key);
                redisTemplate.delete(keys);
            }
        }
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    /**
     * 获取redis中以某些字符串为前缀的key列表
     * @param pattern
     * @return
     */
    public Set keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 获取redis中以某些字符串为前缀的key的缓存对象
     * @param pattern 匹配规则
     * @return
     */
    public List<Object> multiGet(String pattern) {
        Set keys = this.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            return redisTemplate.opsForValue().multiGet(keys);
        }
        return null;
    }
}