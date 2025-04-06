package com.bpm.example.demo4.listener;

import com.bpm.example.demo4.event.CustomFlowableEventType;
import com.bpm.example.demo4.event.FlowableTaskUrgingEvent;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

@Slf4j
public class TaskUrgingEventListener implements FlowableEventListener {
    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEventType eventType = event.getType();
        if (eventType.equals(FlowableEngineEventType.CUSTOM)) {
            if (event instanceof FlowableTaskUrgingEvent) {
                FlowableTaskUrgingEvent flowableTaskUrgingEvent
                        = (FlowableTaskUrgingEvent) event;
                Object entityObject = flowableTaskUrgingEvent.getEntity();
                if (flowableTaskUrgingEvent.getCustomFlowableEventType()
                        .equals(CustomFlowableEventType.TASK_URGING)) {
                    TaskEntity task = (TaskEntity)entityObject;
                    log.info("{}被催办了！", task.getName());
                }
            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}