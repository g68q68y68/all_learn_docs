package com.bpm.example.demo2.service;

import com.bpm.example.demo2.cmd.TaskRecallCmd;
import lombok.AllArgsConstructor;
import org.flowable.engine.ManagementService;

@AllArgsConstructor
public class TaskRecallService {
    protected ManagementService managementService;

    public void executeRecall(String taskId) {
        //实例化撤回Command类
        TaskRecallCmd taskRecallCmd = new TaskRecallCmd(taskId);
        //通过ManagementService管理服务执行撤回Command类
        managementService.executeCommand(taskRecallCmd);
    }
}
