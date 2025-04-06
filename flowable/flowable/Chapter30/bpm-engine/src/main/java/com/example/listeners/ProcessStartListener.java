package com.example.listeners;

import com.example.extend.IdRouterService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessStartListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private IdRouterService idRouterService;


    @Override
    protected void processCreated(FlowableEngineEntityEvent event) {
        //将流程实例ID路由信息写入路由表
        idRouterService.addProcessId(event.getProcessInstanceId());
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }
}
