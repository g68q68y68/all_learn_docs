package com.bpm.example.executionlistener.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

@Slf4j
public class MyUserTaskExecutionListener implements ExecutionListener {
    private transient Expression fixedValue;
    private transient Expression dynamicValue;

    @Override
    public void notify(DelegateExecution execution) {
        //获取用户任务的唯一标识
        String activityId = execution.getCurrentFlowElement().getId();
        //获取事件名称
        String eventName = execution.getEventName();
        log.info("通过class指定的监听器：Id为{}用户任务的{}事件触发", activityId, eventName);
        log.info("fixedValue属性值为：{}", fixedValue.getValue(execution));
        log.info("dynamicValue属性值为：{}", dynamicValue.getValue(execution));
    }
}
