package com.bpm.example.tasklistener.demo.bean;

import lombok.extern.slf4j.Slf4j;
import org.flowable.task.service.delegate.DelegateTask;

import java.io.Serializable;

@Slf4j
public class MyTaskListenerBean implements Serializable {
    public void printInfo(DelegateTask task) {
        //获取任务节点定义key
        String taskDefinitionKey = task.getTaskDefinitionKey();
        //获取事件名称
        String eventName = task.getEventName();
        log.info("通过expression指定的监听器：用户任务{}的{}事件触发", taskDefinitionKey, eventName);
    }
}
