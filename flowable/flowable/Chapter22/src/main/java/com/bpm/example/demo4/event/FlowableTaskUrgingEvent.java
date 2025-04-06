package com.bpm.example.demo4.event;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;

public abstract interface FlowableTaskUrgingEvent extends FlowableEvent {
    public Object getEntity();
    public CustomFlowableEventType getCustomFlowableEventType();
}

