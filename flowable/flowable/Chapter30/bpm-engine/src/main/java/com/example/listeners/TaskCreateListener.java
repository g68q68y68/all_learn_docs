package com.example.listeners;

import com.example.extend.IdRouterService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskCreateListener extends AbstractFlowableEngineEventListener {
    @Autowired
    private IdRouterService idRouterService;

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        TaskEntity taskEntity = (TaskEntity) (event).getEntity();
        //将任务ID路由信息写入路由表
        idRouterService.addTaskId(taskEntity.getId());
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }
}
