package com.bpm.example.demo3.service;

import com.bpm.example.demo3.cmd.DeleteProcessInstanceCmd;
import com.bpm.example.demo3.cmd.ReCreateProcessInstanceCmd;
import lombok.AllArgsConstructor;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.Map;

@AllArgsConstructor
public class RevokeProcessInstanceService {
    protected ManagementService managementService;

    public ProcessInstance executeRevoke(String processInstanceId) {
        //实例化删除流程实例Command类
        DeleteProcessInstanceCmd deleteProcessInstanceCmd
                = new DeleteProcessInstanceCmd(processInstanceId);
        //通过ManagementService管理服务执行删除流程实例Command类
        Map<String, Object> procInstMap = managementService
                .executeCommand(deleteProcessInstanceCmd);
        //实例化重建流程实例Command类
        ReCreateProcessInstanceCmd reCreateProcessInstanceCmd
                = new ReCreateProcessInstanceCmd(procInstMap);
        //通过ManagementService管理服务执行重建流程实例Command类
        ProcessInstance procInst = managementService.executeCommand(reCreateProcessInstanceCmd);
        return procInst;
    }
}