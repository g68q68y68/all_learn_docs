package com.example.cache;

import com.example.client.ProcessDefinitionClient;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;


@Component
@Slf4j
public class RedisProcessDefinitionCache implements
        DeploymentCache<ProcessDefinitionCacheEntry> {
    @Resource(name = "processDefinitionCacheRedisTemplate")
    private RedisTemplate<String, ProcessDefinitionCacheEntry> redisTemplate;

    @Value("${bpm.process-definitions.cache.key}")
    private String processDefinitionCacheKey;

    @Autowired
    private ProcessDefinitionClient processDefinitionClient;

    @Override
    public ProcessDefinitionCacheEntry get(String id) {
        log.info("Query cache from redis: id={}", id);
        Object obj = redisTemplate.opsForHash().get(processDefinitionCacheKey, id);
        if (obj == null) {
            log.info("Sync cache to redis. id={}", id);
            processDefinitionClient.syncProcessDefinition(id);
            obj = redisTemplate.opsForHash().get(processDefinitionCacheKey, id);
        }
        if (obj == null) {
            throw new FlowableObjectNotFoundException("流程定义ID：" + id + "；不存在");
        }
        return (ProcessDefinitionCacheEntry) obj;
    }

    @Override
    public boolean contains(String id) {
        return redisTemplate.opsForHash().hasKey(processDefinitionCacheKey, id);
    }

    @Override
    public void add(String id, ProcessDefinitionCacheEntry object) {
        throw new FlowableException("不支持的操作");
    }

    @Override
    public void remove(String id) {
        throw new FlowableException("不支持的操作");
    }

    @Override
    public void clear() {
        throw new FlowableException("不支持的操作");
    }

    @Override
    public Collection<ProcessDefinitionCacheEntry> getAll() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}

