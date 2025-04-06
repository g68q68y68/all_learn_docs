package com.bpm.example.tasklistener.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

@Slf4j
public class MyTaskListener2 implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //获取任务节点定义key
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        //获取事件名称
        String eventName = delegateTask.getEventName();
        log.info("通过delegateExpression指定的监听器：用户任务{}的{}事件触发",
                taskDefinitionKey, eventName);
    }
}