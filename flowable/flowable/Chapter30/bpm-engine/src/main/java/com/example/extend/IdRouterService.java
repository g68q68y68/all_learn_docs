package com.example.extend;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IdRouterService {
    private Logger log =  LoggerFactory.getLogger(IdRouterService.class);
    private static final String PIKA_PREFIX_PROCESS_ID = "BPM#ENGINE#PROCESSID";
    private static final String PIKA_PREFIX_TASK_ID = "BPM#ENGINE#TASKID";

    @Resource(name = "pikaTemplate")
    private RedisTemplate<String, Object> pikaTemplate;

    @Value("${bpm.engine-name:default}")
    private String engineName;

    public void addProcessId(String processInstanceId) {
        put(PIKA_PREFIX_PROCESS_ID, processInstanceId, engineName);
    }

    public void addTaskId(String taskId) {
        put(PIKA_PREFIX_TASK_ID, taskId, engineName);
    }

    private void put(String key, String hashKey, String value) {
        log.info("Insert Pika success.key={},hashKey={}", key, hashKey);
        pikaTemplate.opsForHash().put(key, hashKey, value);
    }

    public String getProcessEngineName(String processInstanceId) {
        Object value = pikaTemplate.opsForHash().get(PIKA_PREFIX_PROCESS_ID, processInstanceId);
        return value == null ? "" : value.toString();
    }

    public String getTaskEngineName(String taskId) {
        Object value = pikaTemplate.opsForHash().get(PIKA_PREFIX_TASK_ID, taskId);
        return value == null ? "" : value.toString();
    }

    public void deleteProcessId(String processInstanceId) {
        pikaTemplate.opsForHash().delete(PIKA_PREFIX_PROCESS_ID, processInstanceId);
        log.info("Delete Pika success.processInstanceId={}", processInstanceId);
    }

    public void deleteTaskId(String taskId) {
        pikaTemplate.opsForHash().delete(PIKA_PREFIX_TASK_ID, taskId);
    }
}
