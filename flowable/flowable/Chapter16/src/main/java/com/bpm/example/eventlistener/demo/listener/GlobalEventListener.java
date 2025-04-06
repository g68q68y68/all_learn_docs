package com.bpm.example.eventlistener.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

@Slf4j
public class GlobalEventListener implements FlowableEventListener {
    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventType eventType = (FlowableEngineEventType) event.getType();
        switch (eventType) {
            case ENGINE_CREATED:   //流程引擎创建
                exectueEngineEvent(event, eventType);
                break;
            case ENGINE_CLOSED:    //流程引擎销毁
                exectueEngineEvent(event, eventType);
                break;
            case PROCESS_STARTED:  //流程实例发起
                exectueProcessEvent(event, eventType);
                break;
            case PROCESS_COMPLETED://流程实例结束
                exectueProcessEvent(event, eventType);
                break;
            case ACTIVITY_STARTED: //一个节点创建
                exectueActitityEvent(event, eventType);
                break;
            case ACTIVITY_COMPLETED://一个节点结束
                exectueActitityEvent(event, eventType);
                break;
            case TASK_CREATED:   //一个用户任务创建
                exectueTaskEvent(event, eventType);
                break;
            case TASK_ASSIGNED:  //一个用户任务分配办理人
                exectueTaskEvent(event, eventType);
                break;
            case TASK_COMPLETED: //一个用户任务办理完成
                exectueTaskEvent(event, eventType);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }

    private void exectueActitityEvent(FlowableEvent event, FlowableEventType eventType) {
        FlowableActivityEvent activityEvent = (FlowableActivityEvent) event;
        log.info("流程活动{}的{}事件触发", activityEvent.getActivityId(), eventType.name());
    }

    private void exectueEngineEvent(FlowableEvent event, FlowableEventType eventType) {
        log.info("流程引擎的{}事件触发", eventType.name());
    }

    private void exectueProcessEvent(FlowableEvent event, FlowableEventType eventType) {
        FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        Object entityObject = entityEvent.getEntity();
        ProcessInstance processInstance = (ProcessInstance) entityObject;
        log.info("processInstanceId为{}的流程实例的{}事件触发",
                processInstance.getProcessInstanceId(), eventType.name());
    }

    private void exectueTaskEvent(FlowableEvent event, FlowableEventType eventType) {
        FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        Object entityObject = entityEvent.getEntity();
        TaskEntity taskEntity = (TaskEntity) entityObject;
        log.info("用户任务{}的{}事件触发", taskEntity.getTaskDefinitionKey(), eventType.name());
    }
}