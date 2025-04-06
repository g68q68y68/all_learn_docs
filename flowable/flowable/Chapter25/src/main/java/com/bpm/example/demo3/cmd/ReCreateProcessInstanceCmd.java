package com.bpm.example.demo3.cmd;

import lombok.AllArgsConstructor;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.runtime.ProcessInstanceBuilderImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;

import java.util.Map;

@AllArgsConstructor
public class ReCreateProcessInstanceCmd implements Command<ProcessInstance> {
    protected Map<String, Object> procInstMap;

    public ProcessInstance execute(CommandContext commandContext) {
        RuntimeService runtimeService = CommandContextUtil
                .getProcessEngineConfiguration(commandContext).getRuntimeService();
        //重建流程
        ProcessInstanceBuilder processInstanceBuilder = runtimeService
                .createProcessInstanceBuilder();
        ProcessInstance newProcessInstance = processInstanceBuilder
                .processDefinitionId((String)procInstMap.get("processDefinitionId"))
                .processDefinitionKey((String)procInstMap.get("processDefinitionKey"))
                .predefineProcessInstanceId((String)procInstMap.get("processInstanceId"))
                .businessKey((String)procInstMap.get("businessKey"))
                .variables((Map)procInstMap.get("variables"))
                .tenantId((String)procInstMap.get("tenantId"))
                .start();
        return newProcessInstance;
    }
}
