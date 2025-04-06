package com.bpm.example.demo2.cache;

import com.bpm.example.demo2.util.RedisClient;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
public class CustomProcessDeploymentCache
        implements DeploymentCache<ProcessDefinitionCacheEntry> {
    //Redis客户端工具
    private RedisClient redisClient;
    //Caffeine缓存管理器
    private CaffeineCacheManager caffeineCacheManager;
    //流程定义前缀标识
    private String processDefinitonCacheKeyPrefix;

    /**
     * 添加流程定义缓存
     *
     * @param id         流程定义编号
     * @param cacheEntry 流程定义缓存对象
     * @return
     */
    @Override
    public void add(String id, ProcessDefinitionCacheEntry cacheEntry) {
        try {
            redisClient.set(processDefinitonCacheKeyPrefix + id, cacheEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCaffeineCache().put(processDefinitonCacheKeyPrefix + id, cacheEntry);
    }

    /**
     * 查询流程定义缓存
     *
     * @param id 流程定义编号
     * @return 流程定义缓存对象
     */
    @Override
    public ProcessDefinitionCacheEntry get(String id) {
        ProcessDefinitionCacheEntry cacheEntry = null;
        try {
            cacheEntry = (ProcessDefinitionCacheEntry) redisClient
                    .get(processDefinitonCacheKeyPrefix + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.isNull(cacheEntry)) {
            cacheEntry = getCaffeineCache().get(processDefinitonCacheKeyPrefix + id,
                    ProcessDefinitionCacheEntry.class);
        }
        return cacheEntry;
    }

    /**
     * 删除流程定义缓存
     *
     * @param id 流程定义编号
     */
    @Override
    public void remove(String id) {
        try {
            redisClient.del(processDefinitonCacheKeyPrefix + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCaffeineCache().evictIfPresent(processDefinitonCacheKeyPrefix + id);
    }

    /**
     * 清除所有流程定义缓存
     */
    @Override
    public void clear() {
        try {
            redisClient.del((String[]) redisClient
                    .keys(processDefinitonCacheKeyPrefix + "*")
                    .toArray(new String[]{}));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCaffeineCache().clear();
    }

    /**
     * 校验redis中是否存在以id为key的流程定义缓存
     *
     * @param id 流程定义编号
     * @return
     */
    @Override
    public boolean contains(String id) {
        boolean result = false;
        try {
            result = redisClient.hasKey(processDefinitonCacheKeyPrefix + id);
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 查询所有流程定义缓存
     *
     * @return
     */
    @Override
    public Collection<ProcessDefinitionCacheEntry> getAll() {
        List<ProcessDefinitionCacheEntry> cacheEntries = new ArrayList<>();
        try {
            List<Object> objects = redisClient
                    .multiGet(processDefinitonCacheKeyPrefix + "*");
            if (CollectionUtils.isEmpty(objects)) {
                objects.forEach(object -> {
                    cacheEntries.add((ProcessDefinitionCacheEntry) objects);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.isNull(cacheEntries)) {
            return null;
        }
        return cacheEntries;
    }

    /**
     * 查询所有流程定义缓存数
     *
     * @return
     */
    @Override
    public int size() {
        Set keys = redisClient.keys(processDefinitonCacheKeyPrefix + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            return keys.size();
        }
        return 0;
    }

    private Cache getCaffeineCache() {
        return caffeineCacheManager.getCache("default");
    }
}