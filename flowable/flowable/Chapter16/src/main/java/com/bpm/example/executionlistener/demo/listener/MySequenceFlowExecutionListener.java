package com.bpm.example.executionlistener.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

@Slf4j
public class MySequenceFlowExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        //获取顺序流的唯一标识
        String activityId = execution.getCurrentFlowElement().getId();
        //获取事件名称
        String eventName = execution.getEventName();
        log.info("通过delegateExpression指定的监听器：Id为{}的顺序流的{}事件触发",
                activityId, eventName);
    }
}