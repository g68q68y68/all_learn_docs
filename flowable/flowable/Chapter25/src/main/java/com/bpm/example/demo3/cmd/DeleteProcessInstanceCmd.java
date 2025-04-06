package com.bpm.example.demo3.cmd;

import lombok.AllArgsConstructor;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DeleteProcessInstanceCmd implements Command<Map<String, Object>> {
    //流程实例编号
    protected String processInstanceId;

    public Map<String, Object> execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl procEngineConf = CommandContextUtil
                .getProcessEngineConfiguration(commandContext);
        RuntimeService runtimeService = procEngineConf.getRuntimeService();
        //根据processInstanceId查询流程实例
        ProcessInstance procInst = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (procInst == null) {
            throw new FlowableObjectNotFoundException("编号为" + processInstanceId +
                    "的流程实例不存在。", ProcessInstance.class);
        }
        //不是流程发起者不能撤销流程
        String authenticatedUserId = Authentication.getAuthenticatedUserId();
        if (!procInst.getStartUserId().equals(authenticatedUserId)) {
            throw new FlowableException("非流程发起者不能撤销流程。");
        }
        //查询流程变量
        Map<String, Object> varMap = runtimeService.getVariables(processInstanceId);
        //删除流程实例
        runtimeService.deleteProcessInstance(processInstanceId, "流程撤销删除流程");
        //删除历史流程实例
        HistoryService historyService = procEngineConf.getHistoryService();
        historyService.deleteHistoricProcessInstance(processInstanceId);
        Map<String, Object> procInstMap = new HashMap<>();
        procInstMap.put("processInstanceId", processInstanceId);
        procInstMap.put("processDefinitionId", procInst.getProcessDefinitionId());
        procInstMap.put("processDefinitionKey", procInst.getProcessDefinitionKey());
        procInstMap.put("businessKey", procInst.getBusinessKey());
        procInstMap.put("tenantId", procInst.getTenantId());
        procInstMap.put("variables", varMap);
        return procInstMap;
    }
}
