package com.bpm.example.demo1.service;

import com.bpm.example.demo1.cmd.RestartProcessInstanceCmd;
import lombok.AllArgsConstructor;
import org.flowable.engine.ManagementService;

import java.util.List;

@AllArgsConstructor
public class RestartProcessInstanceService {
    protected ManagementService managementService;

    public void executeRestart(String processInstanceId, List<String> activityIds) {
        //实例化流程复活Command类
        RestartProcessInstanceCmd restartProcessInstance
                = new RestartProcessInstanceCmd(processInstanceId, activityIds);
        //通过ManagementService管理服务执行流程复活Command类
        managementService.executeCommand(restartProcessInstance);
    }
}
