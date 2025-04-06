package com.bpm.example.demo3.util;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MultiInstanceExecutionUtil {
    //流程存储服务
    protected RepositoryService repositoryService;
    //运行时服务
    protected RuntimeService runtimeService;
    //任务服务
    protected TaskService taskService;

    /**
     * 加签
     *
     * @param collectionName  多实例配置中flowable:collection配置的变量名
     * @param elementVariable 多实例配置中flowable:elementVariable配置的变量名
     * @param procInstId      流程实例编号
     * @param currentTask     操作减签任务的Task
     * @param userIds         加签用户列表
     */
    public void addSign(String collectionName, String elementVariable, String procInstId,
                        Task currentTask, List<String> userIds) {
        //必要的参数校验
        if (StringUtils.isBlank(collectionName) || StringUtils.isBlank(elementVariable)
                || StringUtils.isBlank(procInstId)) {
            throw new FlowableIllegalArgumentException
                    ("参数collectionName、elementVariable和processInstanceId均不能为空");
        }
        if (CollectionUtils.isEmpty(userIds)) {
            throw new FlowableIllegalArgumentException("参数userIds不能为空");
        }
        //查询并重设多实例办理人集合的变量
        List<String> assigneeList = (List) runtimeService
                .getVariable(procInstId, collectionName);
        assigneeList.addAll(userIds);
        runtimeService.setVariable(procInstId, collectionName, assigneeList);
        //查询多实例的根执行实例
        Execution miExecution = getMultiInstanceRootExecution(procInstId,
                currentTask.getTaskDefinitionKey());
        //查询并重设多实例的总实例数
        Integer nrOfInstances = (Integer) runtimeService
                .getVariableLocal(miExecution.getId(), "nrOfInstances");
        runtimeService.setVariableLocal(miExecution.getId(), "nrOfInstances",
                nrOfInstances + userIds.size());
        //查询多实例的参数
        MultiInstanceLoopCharacteristics loopCh = getLoopCharacteristics(procInstId,
                currentTask.getTaskDefinitionKey());
        if (!loopCh.isSequential()) {
            for (String userId : userIds) {
                //并行多实例要查询并重设多实例的激活实例数
                Integer nrOfActiveInstances = (Integer) runtimeService
                        .getVariableLocal(miExecution.getId(), "nrOfActiveInstances");
                runtimeService.setVariableLocal(miExecution.getId(),
                        "nrOfActiveInstances", nrOfActiveInstances + 1);
                //并行多实例要创建执行实例和相关的对象
                runtimeService
                        .addMultiInstanceExecution(currentTask.getTaskDefinitionKey(),
                                procInstId, ImmutableMap.of(elementVariable, userId));
            }
        }
    }

    /**
     * 减签
     *
     * @param collectionName 多实例配置中flowable:collection配置的变量名
     * @param procInstId     流程实例编号
     * @param currentTask    操作减签任务的Task
     * @param userId         减签用户
     */
    public void deleteSign(String collectionName, String procInstId, Task currentTask,
                           String userId) {
        //必要的参数校验
        if (StringUtils.isBlank(collectionName) || StringUtils.isBlank(procInstId)
                || StringUtils.isBlank(userId)) {
            throw new FlowableIllegalArgumentException
                    ("参数collectionName、processInstanceId和userId均不能为空");
        }
        //查询多实例的根执行实例
        Execution miExecution = getMultiInstanceRootExecution(procInstId,
                currentTask.getTaskDefinitionKey());
        //查询并重设多实例的总实例数
        Integer nrOfInstances = (Integer) runtimeService
                .getVariableLocal(miExecution.getId(), "nrOfInstances");
        runtimeService.setVariableLocal(miExecution.getId(), "nrOfInstances",
                nrOfInstances - 1);
        //查询多实例的参数
        MultiInstanceLoopCharacteristics loopCharacteristics
                = getLoopCharacteristics(procInstId, currentTask.getTaskDefinitionKey());
        List<String> assigneeList = (List) runtimeService
                .getVariable(procInstId, collectionName);
        if (loopCharacteristics.isSequential()) {
            //串行多实例校验被减签人是否在会签后续办理人中
            Integer loopCounter = (Integer) runtimeService
                    .getVariableLocal(currentTask.getExecutionId(), "loopCounter");
            if (!assigneeList.subList(loopCounter, assigneeList.size() - 1)
                    .contains(userId)) {
                throw new FlowableException("会签后续办理者中没有用户" + userId);
            }
        } else {
            //并行多实例校验被减签人是否是会签的运行时任务的办理人
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(currentTask.getProcessInstanceId())
                    .taskDefinitionKey(currentTask.getTaskDefinitionKey())
                    .active().list();
            if (!tasks.stream().map(e -> e.getAssignee()).collect(Collectors.toList())
                    .contains(userId)) {
                throw new FlowableException("没有用户" + userId + "的会签任务");
            }
            //并行多实例要查询并重设多实例的激活实例数
            Integer nrOfActiveInstances = (Integer) runtimeService
                    .getVariableLocal(miExecution.getId(), "nrOfActiveInstances");
            runtimeService.setVariableLocal(miExecution.getId(), "nrOfActiveInstances",
                    nrOfActiveInstances - 1);
            //并行多实例要删除被减签人所拥有任务的执行实例
            Task deleteTask = taskService.createTaskQuery()
                    .processInstanceId(procInstId)
                    .taskDefinitionKey(currentTask.getTaskDefinitionKey())
                    .taskAssignee(userId).singleResult();
            runtimeService.deleteMultiInstanceExecution(deleteTask.getExecutionId(),
                    false);
        }
        //重设多实例办理人集合的变量
        assigneeList.remove(userId);
        runtimeService.setVariable(procInstId, collectionName, assigneeList);
    }

    //查询多实例的参数
    private MultiInstanceLoopCharacteristics getLoopCharacteristics(
            String processInstanceId, String taskDefinitionKey) {
        ProcessInstance procInst = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (procInst == null) {
            throw new FlowableObjectNotFoundException("id为" + processInstanceId + "的流程实例不存在");
        }
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(procInst.getProcessDefinitionId());
        MultiInstanceLoopCharacteristics loopCharacteristics
                = ((UserTask) (bpmnModel.getFlowElement(taskDefinitionKey)))
                .getLoopCharacteristics();
        if (loopCharacteristics == null) {
            throw new FlowableIllegalArgumentException("该节点不是多实例用户任务");
        }
        return loopCharacteristics;
    }

    //查询多实例的根执行实例
    private Execution getMultiInstanceRootExecution(String procInstId,
                                                    String taskDefinitionKey) {
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(procInstId).activityId(taskDefinitionKey).list();
        Execution multiInstanceRootExecution = null;
        Execution currentExecution = executions.get(0);
        while (currentExecution != null && multiInstanceRootExecution == null &&
                currentExecution.getParentId() != null) {
            if (((ExecutionEntity) currentExecution).isMultiInstanceRoot()) {
                multiInstanceRootExecution = currentExecution;
            } else {
                currentExecution = runtimeService.createExecutionQuery()
                        .executionId(currentExecution.getParentId()).singleResult();
            }
        }
        return multiInstanceRootExecution;
    }
}
