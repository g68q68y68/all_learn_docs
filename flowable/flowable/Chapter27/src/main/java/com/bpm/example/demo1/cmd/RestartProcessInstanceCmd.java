package com.bpm.example.demo1.cmd;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RestartProcessInstanceCmd implements Command<ProcessInstance> {
    //待复活的流程实例编号
    protected String processInstanceId;
    //要复活到的节点列表
    protected List<String> activityIds;

    @Override
    public ProcessInstance execute(CommandContext commandContext) {
        ProcessEngineConfiguration procEngineConf = CommandContextUtil
                .getProcessEngineConfiguration(commandContext);
        HistoryService historyService = procEngineConf.getHistoryService();
        //校验和查询历史流程实例
        HistoricProcessInstance hisProcInst
                = checkAndGetHistoricProcessInstance(historyService);
        //校验待复活节点
        checkActivityIds(hisProcInst.getProcessDefinitionId());
        //校验流程定义
        ProcessDefinition processDefinition = ProcessDefinitionUtil
                .getProcessDefinition(hisProcInst.getProcessDefinitionId());
        if (processDefinition == null) {
            throw new FlowableException("流程定义不存在");
        }
        //获取流程启动节点
        Process process = ProcessDefinitionUtil.getProcess(processDefinition.getId());
        StartEvent initialFlowElement = (StartEvent) process.getInitialFlowElement();
        //重建运行时流程实例的主执行实例
        ExecutionEntity processInstance = reCreateProcessInstance(processDefinition,
                initialFlowElement, hisProcInst);
        //创建子执行实例
        ExecutionEntity childExecution = CommandContextUtil.getExecutionEntityManager()
                .createChildExecution(processInstance);
        childExecution.setCurrentFlowElement(initialFlowElement);
        //收集待复活的流程变量
        Map<String, Object> varMap = collectVariables(historyService, hisProcInst);
        //设置历史流程实例结束节点和结束时间为空
        ((HistoricProcessInstanceEntityImpl) hisProcInst).setEndActivityId(null);
        ((HistoricProcessInstanceEntityImpl) hisProcInst).setEndTime(null);
        //执行动态跳转
        procEngineConf.getRuntimeService()
                .createChangeActivityStateBuilder()
                .processInstanceId(processInstance.getProcessInstanceId())
                .moveSingleExecutionToActivityIds(childExecution.getId(), activityIds)
                .processVariables(varMap)
                .changeState();
        return procEngineConf.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(processInstance.getId()).singleResult();
    }

    /**
     * 校验和查询历史流程实例
     *
     * @param historyService
     * @return
     */
    private HistoricProcessInstance checkAndGetHistoricProcessInstance(
            HistoryService historyService) {
        if (StringUtils.isBlank(processInstanceId)) {
            throw new FlowableIllegalArgumentException("processInstanceId不能为空");
        }
        HistoricProcessInstance hisProcInst = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (hisProcInst == null) {
            throw new FlowableException("id为" + processInstanceId + "的流程实例不存在");
        } else if (hisProcInst != null && hisProcInst.getEndTime() == null) {
            throw new FlowableException("id为" + processInstanceId + "的流程实例没有结束");
        }
        return hisProcInst;
    }

    /**
     * 校验复活节点
     *
     * @param processDefinitionId
     */
    private void checkActivityIds(String processDefinitionId) {
        if (CollectionUtils.isEmpty(activityIds)) {
            throw new FlowableIllegalArgumentException("activityIds不能为空");
        }
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
        List<String> notExistedFlowElements = new ArrayList<>();
        for (String activityId : activityIds) {
            if (!bpmnModel.getMainProcess().containsFlowElementId(activityId)) {
                notExistedFlowElements.add(activityId);
            }
        }
        if (!CollectionUtils.isEmpty(notExistedFlowElements)) {
            throw new FlowableIllegalArgumentException("Id为" + String.join("、",
                    notExistedFlowElements) + "节点不存在");
        }
    }

    /**
     * 重建流程实例
     *
     * @param processDefinition
     * @param initialFlowElement
     * @param hisProcInst
     * @return
     */
    private ExecutionEntity reCreateProcessInstance(ProcessDefinition processDefinition,
                                                    StartEvent initialFlowElement,
                                                    HistoricProcessInstance hisProcInst) {
        //创建流程实例
        ExecutionEntity procInst = CommandContextUtil.getExecutionEntityManager()
                .createProcessInstanceExecution(processDefinition, hisProcInst.getId(),
                        hisProcInst.getBusinessKey(), hisProcInst.getBusinessStatus(),
                        hisProcInst.getName(), hisProcInst.getCallbackId(),
                        hisProcInst.getCallbackType(), hisProcInst.getReferenceId(),
                        hisProcInst.getReferenceType(),
                        hisProcInst.getPropagatedStageInstanceId(),
                        hisProcInst.getTenantId(), initialFlowElement.getInitiator(),
                        hisProcInst.getStartActivityId());
        //重设流程开始事件
        procInst.setStartTime(hisProcInst.getStartTime());
        return procInst;
    }

    /**
     * 收集待复活的流程变量
     *
     * @param historyService
     * @param hisProcInst
     * @return
     */
    private Map<String, Object> collectVariables(HistoryService historyService,
                                                 HistoricProcessInstance hisProcInst) {
        Map<String, Object> varMap = new HashMap<>();
        //查询历史流程变量
        List<HistoricVariableInstance> hisVariables = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(hisProcInst.getId())
                .executionId(hisProcInst.getId()).list();
        for (HistoricVariableInstance hisVariable : hisVariables) {
            varMap.put(hisVariable.getVariableName(), hisVariable.getValue());
        }
        return varMap;
    }
}
