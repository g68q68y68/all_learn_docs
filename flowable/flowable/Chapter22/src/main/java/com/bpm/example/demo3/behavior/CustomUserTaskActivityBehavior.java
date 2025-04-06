package com.bpm.example.demo3.behavior;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.Context;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomUserTaskActivityBehavior extends UserTaskActivityBehavior {
    public CustomUserTaskActivityBehavior(UserTask userTask) {
        super(userTask);
    }

    @Override
    public void execute(DelegateExecution execution) {
        ProcessEngineConfigurationImpl engineConf = Context
                .getProcessEngineConfiguration();
        TaskEntityManager taskEntityManager = engineConf.getTaskServiceConfiguration()
                .getTaskEntityManager();

        //第1步，创建任务实例对象
        TaskEntity task = taskEntityManager.create();
        task.setProcessInstanceId(execution.getProcessInstanceId());
        task.setExecutionId(execution.getId());
        task.setTaskDefinitionKey(userTask.getId());
        task.setName(userTask.getName());
        task.setDescription(userTask.getDocumentation());
        taskEntityManager.insert(task, true);

        ExpressionManager expressionManager = engineConf.getExpressionManager();
        //第2步，查询用户任务节点人员配置
        String activeTaskAssignee = userTask.getAssignee();
        List<String> activeTaskCandidateUsers = userTask.getCandidateUsers();
        //第3步，分配任务办理人及候选人
        handleAssignments(taskEntityManager, activeTaskAssignee, activeTaskCandidateUsers,
                task, expressionManager, execution);

        //第4步，触发任务创建监听器
        engineConf.getListenerNotificationHelper()
                .executeTaskListeners(task, TaskListener.EVENTNAME_CREATE);

        //第5步，发送任务创建事件
        if (Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
            Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                    FlowableEventBuilder.createEntityEvent(FlowableEngineEventType
                            .TASK_CREATED, task), engineConf.getEngineCfgKey());
        }
    }

    /**
     * 分配办理人
     *
     * @param taskEntityManager
     * @param assignee
     * @param candidateUsers
     * @param task
     * @param expressionManager
     * @param execution
     */
    private void handleAssignments(TaskEntityManager taskEntityManager, String assignee,
                                   List<String> candidateUsers, TaskEntity task,
                                   ExpressionManager expressionManager,
                                   DelegateExecution execution) {
        boolean isSetAssignee = false;
        //分配任务办理人
        if (StringUtils.isNotEmpty(assignee)) {
            Object assigneeExpressionValue = expressionManager.createExpression(assignee)
                    .getValue(execution);
            String assigneeValue = null;
            if (assigneeExpressionValue != null) {
                assigneeValue = assigneeExpressionValue.toString();
            }
            if (StringUtils.isNoneBlank(assigneeValue)) {
                taskEntityManager.changeTaskAssignee(task, assigneeValue);
                isSetAssignee = true;
            }
        }

        //分配任务候选人
        if (!isSetAssignee && candidateUsers != null && !candidateUsers.isEmpty()) {
            List<String> allCandidates = new ArrayList<>();
            for (String candidateUser : candidateUsers) {
                Expression userIdExpr = expressionManager
                        .createExpression(candidateUser);
                Object value = userIdExpr.getValue(execution);
                if (value instanceof String) {
                    Collection<String> candidates = extractCandidates((String) value);
                    allCandidates.addAll(candidates);
                } else if (value instanceof Collection) {
                    allCandidates.addAll((Collection) value);
                } else {
                    throw new FlowableException("Expression did not resolve to a string or " +
                            "collection of strings");
                }
            }
            List<String> distinctCandidates = (List) allCandidates.stream().distinct()
                    .collect(Collectors.toList());
            if (distinctCandidates != null) {
                //如果只有一个任务候选人，则设置为办理人
                if (distinctCandidates.size() == 1) {
                    log.info("TaskId为{}的用户任务只有一个候选人，将设置为办理人", task.getId());
                    taskEntityManager.changeTaskAssignee(task, distinctCandidates.get(0));
                    isSetAssignee = true;
                } else {
                    task.addCandidateUsers(distinctCandidates);
                }
            }
        }
    }
}

