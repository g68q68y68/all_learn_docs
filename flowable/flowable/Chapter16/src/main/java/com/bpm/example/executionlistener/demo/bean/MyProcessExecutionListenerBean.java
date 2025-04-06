package com.bpm.example.executionlistener.demo.bean;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;

import java.io.Serializable;

@Slf4j
public class MyProcessExecutionListenerBean implements Serializable {
    public void printInfo(DelegateExecution execution) {
        //获取流程实例编号
        String processInstanceId = execution.getProcessInstanceId();
        //获取事件名称
        String eventName = execution.getEventName();
        log.info("通过expression指定的监听器：processInstanceId为{}的流程实例的{}事件触发",
                processInstanceId, eventName);
    }
}
