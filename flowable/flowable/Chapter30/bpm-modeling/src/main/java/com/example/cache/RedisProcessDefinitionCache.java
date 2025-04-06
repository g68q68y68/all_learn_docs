package com.example.cache;

import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class RedisProcessDefinitionCache implements DeploymentCache<ProcessDefinitionCacheEntry> {

    private Logger log = LoggerFactory.getLogger(RedisProcessDefinitionCache.class);

    @Resource(name = "processDefinitionCacheRedisTemplate")
    private RedisTemplate<String, ProcessDefinitionCacheEntry> redisTemplate;

    @Value("${activiti.process-definitions.cache.key}")
    private String processDefinitionCacheKey;

    @Override
    public ProcessDefinitionCacheEntry get(String id) {
        log.info("Query cache from redis: id={}", id);
        Object obj = redisTemplate.opsForHash().get(processDefinitionCacheKey, id);
        return (ProcessDefinitionCacheEntry) obj;
    }

    @Override
    public boolean contains(String id) {
        return redisTemplate.opsForHash().hasKey(processDefinitionCacheKey, id);
    }

    @Override
    public void add(String id, ProcessDefinitionCacheEntry object) {
        log.info("Add cache to redis: id={}", id);
        redisTemplate.opsForHash().put(processDefinitionCacheKey, id, object);
    }

    @Override
    public void remove(String id) {
        log.info("Remove cache from redis: id={}", id);
        redisTemplate.opsForHash().delete(processDefinitionCacheKey, id);
    }

    @Override
    public void clear() {
        log.info("Clear cache");
        redisTemplate.delete(processDefinitionCacheKey);
    }
}
