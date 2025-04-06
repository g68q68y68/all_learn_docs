package com.bpm.example.demo2.service;

import com.bpm.example.demo2.cmd.InjectUserTaskInProcessInstanceCmd;
import lombok.AllArgsConstructor;
import org.flowable.engine.ManagementService;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;

@AllArgsConstructor
public class InjectUserTaskInProcessInstanceService {
    protected ManagementService managementService;

    public void executeInjectUserTaskInProcessInstance(String currentTaskId,
                                                       String newTaskKey, String newTaskName,
                                                       String newTaskAssignee) {
        //初始化DynamicUserTaskBuilder
        DynamicUserTaskBuilder dynamiBuilder = new DynamicUserTaskBuilder();
        dynamiBuilder.setId(newTaskKey);
        dynamiBuilder.setName(newTaskName);
        dynamiBuilder.setAssignee(newTaskAssignee);
        //初始化Command类
        InjectUserTaskInProcessInstanceCmd injectUserTaskInProcessInstanceCmd
                = new InjectUserTaskInProcessInstanceCmd(currentTaskId, dynamiBuilder);
        //通过ManagementService管理服务执行动态增加临时节点Command类
        managementService.executeCommand(injectUserTaskInProcessInstanceCmd);
    }
}
