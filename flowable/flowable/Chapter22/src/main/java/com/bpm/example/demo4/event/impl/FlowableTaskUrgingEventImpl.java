package com.bpm.example.demo4.event.impl;

import com.bpm.example.demo4.event.CustomFlowableEventType;
import com.bpm.example.demo4.event.FlowableTaskUrgingEvent;
import lombok.Getter;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.common.engine.impl.event.FlowableEventImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

@Getter
public class FlowableTaskUrgingEventImpl extends FlowableEventImpl
        implements FlowableTaskUrgingEvent {
    protected Object entity;
    protected CustomFlowableEventType customFlowableEventType;

    public FlowableTaskUrgingEventImpl(TaskEntity entity, FlowableEventType type,
                                       CustomFlowableEventType customFlowableEventType) {
        super(type);
        this.entity = entity;
        this.customFlowableEventType = customFlowableEventType;
    }
}