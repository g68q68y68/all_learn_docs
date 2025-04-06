package com.bpm.example.demo2.cmd;

import lombok.AllArgsConstructor;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TaskRecallCmd implements Command<Void> {
    //任务编号
    protected final String taskId;

    public Void execute(CommandContext commandContext) {
        //taskId参数不能为空
        if (this.taskId == null) {
            throw new FlowableIllegalArgumentException("Task id is required");
        }
        ProcessEngineConfigurationImpl procEngineConf = CommandContextUtil
                .getProcessEngineConfiguration(commandContext);
        RuntimeService runtimeService = procEngineConf.getRuntimeService();
        //获取历史服务
        HistoryService historyService = procEngineConf.getHistoryService();
        //根据taskId查询历史任务
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
                .taskId(this.taskId).singleResult();
        //进行一系列任务和流程校验
        basicCheck(runtimeService, task);
        //获取流程模型
        BpmnModel bpmnModel = ProcessDefinitionUtil
                .getBpmnModel(task.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        List<String> nextElementIdList = new ArrayList();
        List<UserTask> nextUserTaskList = new ArrayList();
        //获取后续节点信息
        getNextElementInfo(bpmnModel, flowElement, nextElementIdList, nextUserTaskList);
        //再校验是否后续节点任务是否已经办理完成
        existNextFinishedTaskCheck(historyService, task, nextUserTaskList);
        //清理节点历史
        deleteHistoricActivityInstance(procEngineConf, historyService, task);
        //执行跳转
        List<String> recallElementIdList = getRecallElementIdList(runtimeService, task,
                nextElementIdList);
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdsToSingleActivityId(recallElementIdList, task
                        .getTaskDefinitionKey())
                .changeState();
        return null;
    }

    /**
     * 任务校验
     *
     * @param runtimeService
     * @param task
     */
    private void basicCheck(RuntimeService runtimeService, HistoricTaskInstance task) {
        if (task == null) {
            String msg = "任务不存在";
            throw new FlowableObjectNotFoundException(msg);
        }
        if (task.getEndTime() == null) {
            String msg = "任务正在执行,不需要回退";
            throw new FlowableException(msg);
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
        if (processInstance == null) {
            String msg = "该流程已经结束，无法进行任务回退。";
            throw new FlowableException(msg);
        }
    }

    /**
     * 获取后续节点信息
     *
     * @param bpmnModel        流程模型
     * @param currentNode      当前节点
     * @param nextNodeIdList   后续节点Id列表
     * @param nextUserTaskList 后续用户任务节点列表
     */
    private void getNextElementInfo(BpmnModel bpmnModel, FlowElement currentNode,
                                    List<String> nextNodeIdList,
                                    List<UserTask> nextUserTaskList) {
        //查询当前节点所有流出顺序流
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentNode).getOutgoingFlows();
        for (SequenceFlow flow : outgoingFlows) {
            //后续节点
            FlowElement targetNode = bpmnModel.getFlowElement(flow.getTargetRef());
            nextNodeIdList.add(targetNode.getId());
            if (targetNode instanceof UserTask) {
                nextUserTaskList.add((UserTask) targetNode);
            } else if (targetNode instanceof Gateway) {
                Gateway gateway = ((Gateway) targetNode);
                //网关节点执行递归操作
                getNextElementInfo(bpmnModel, gateway, nextNodeIdList, nextUserTaskList);
            } else {
                //其它类型节点读者可自行实现
            }
        }
    }

    /**
     * 校验是否后续节点任务是否已经办理完成
     *
     * @param historyService      历史服务
     * @param currentTaskInstance 当前任务实例
     * @param nextUserTaskList    后续用户
     */
    private void existNextFinishedTaskCheck(HistoryService historyService,
                                            HistoricTaskInstance currentTaskInstance,
                                            List<UserTask> nextUserTaskList) {
        List<HistoricTaskInstance> hisTaskList = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(currentTaskInstance.getProcessInstanceId())
                .taskCompletedAfter(currentTaskInstance.getEndTime())
                .list();
        List<String> nextUserTaskIdList = nextUserTaskList.stream().map(UserTask::getId)
                .collect(Collectors.toList());
        if (!hisTaskList.isEmpty()) {
            hisTaskList.forEach(obj -> {
                if (nextUserTaskIdList.contains(obj.getTaskDefinitionKey())) {
                    String msg = "存在已完成下一节点任务";
                    throw new FlowableException(msg);
                }
            });
        }
    }

    /**
     * 获取可撤回的节点列表
     *
     * @param runtimeService      流程引擎配置
     * @param currentTaskInstance 任务实例
     * @param nextElementIdList   后续节点列表
     * @return
     */
    private List<String> getRecallElementIdList(RuntimeService runtimeService,
                                                HistoricTaskInstance currentTaskInstance,
                                                List<String> nextElementIdList) {
        List<String> recallElementIdList = new ArrayList();
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(currentTaskInstance.getProcessInstanceId())
                .onlyChildExecutions().list();
        if (!executions.isEmpty()) {
            executions.forEach(obj -> {
                if (nextElementIdList.contains(obj.getActivityId())) {
                    recallElementIdList.add(obj.getActivityId());
                }
            });
        }
        return recallElementIdList;
    }

    /**
     * 清理节点历史
     *
     * @param procEngineConf
     * @param historyService
     * @param task
     */
    private void deleteHistoricActivityInstance(ProcessEngineConfigurationImpl procEngineConf,
                                                HistoryService historyService,
                                                HistoricTaskInstance task) {
        //删除要撤回的节点的历史
        List<HistoricActivityInstance> allHisActivityList = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .activityId(task.getTaskDefinitionKey()).list();
        HistoricActivityInstance hisActivity = allHisActivityList
                .stream().filter(obj -> task.getId().equals(obj.getTaskId()))
                .findFirst().get();
        HistoricActivityInstanceEntityManager hisActivityEntityManager = procEngineConf
                .getHistoricActivityInstanceEntityManager();
        hisActivityEntityManager.delete(hisActivity.getId());
        //删除被撤回的节点的历史
        List<HistoricActivityInstance> hisActivityList = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .startedAfter(task.getEndTime())
                .orderByHistoricActivityInstanceStartTime()
                .asc().list();
        List<String> deleteHisActivityIdList = new ArrayList();
        if (!CollectionUtils.isEmpty(hisActivityList)) {
            hisActivityList.forEach(obj -> {
                if (!deleteHisActivityIdList.contains(obj.getActivityId())) {
                    deleteHisActivityIdList.add(obj.getId());
                    hisActivityEntityManager.delete(obj.getId());
                }
            });
        }
    }
}
