package com.example.demo.extend;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.history.async.AsyncHistoryManager;
import org.flowable.engine.impl.history.async.HistoryJsonConstants;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static org.flowable.job.service.impl.history.async.util.AsyncHistoryJsonUtil.putIfNotNull;

public class CustomAsyncHistoryManager extends AsyncHistoryManager {
    public CustomAsyncHistoryManager(ProcessEngineConfigurationImpl
                                             processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public void updateHistoricActivityInstance(ActivityInstance activityInstance) {
        if (getHistoryConfigurationSettings().isHistoryEnabledForActivity(activityInstance)) {
            if (activityInstance.getExecutionId() != null) {
                ObjectNode data = processEngineConfiguration.getObjectMapper().createObjectNode();
                putIfNotNull(data, HistoryJsonConstants.PROCESS_INSTANCE_ID,
                        activityInstance.getProcessInstanceId());
                putIfNotNull(data, HistoryJsonConstants.RUNTIME_ACTIVITY_INSTANCE_ID,
                        activityInstance.getId());
                putIfNotNull(data, HistoryJsonConstants.TASK_ID, activityInstance.getTaskId());
                putIfNotNull(data, HistoryJsonConstants.ASSIGNEE, activityInstance.getAssignee());
                putIfNotNull(data, HistoryJsonConstants.CALLED_PROCESS_INSTANCE_ID,
                        activityInstance.getCalledProcessInstanceId());
                getAsyncHistorySession().addHistoricData(getJobServiceConfiguration(),
                        HistoryJsonConstants.TYPE_UPDATE_HISTORIC_ACTIVITY_INSTANCE, data);
            }
        }
    }

    @Override
    public void recordTaskInfoChange(TaskEntity taskEntity, String runtimeActivityInstanceId, Date changeTime) {
        if (getHistoryConfigurationSettings().isHistoryEnabledForUserTask(taskEntity)) {
            ObjectNode data = processEngineConfiguration.getObjectMapper().createObjectNode();
            putIfNotNull(data, HistoryJsonConstants.PROCESS_INSTANCE_ID,
                    taskEntity.getProcessInstanceId());
            addCommonTaskFields(taskEntity, null, data);
            getAsyncHistorySession().addHistoricData(getJobServiceConfiguration(), HistoryJsonConstants.TYPE_TASK_PROPERTY_CHANGED, data);
        }

        Map<String, Object> originalPersistentState = (Map<String, Object>) taskEntity.getOriginalPersistentState();

        if ((originalPersistentState == null && taskEntity.getAssignee() != null) ||
                (originalPersistentState != null && !Objects.equals(originalPersistentState.get("assignee"), taskEntity.getAssignee()))) {

            handleTaskAssigneeChange(taskEntity, runtimeActivityInstanceId, changeTime);
        }

        if ((originalPersistentState == null && taskEntity.getOwner() != null) ||
                (originalPersistentState != null && !Objects.equals(originalPersistentState.get("owner"), taskEntity.getOwner()))) {

            handleTaskOwnerChange(taskEntity, runtimeActivityInstanceId, changeTime);
        }
    }

    @Override
    protected void handleTaskAssigneeChange(TaskEntity taskEntity, String activityInstanceId, Date changeTime) {
        if (getHistoryConfigurationSettings().isHistoryEnabledForActivity(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey())) {
            ObjectNode data = processEngineConfiguration.getObjectMapper().createObjectNode();
            putIfNotNull(data, HistoryJsonConstants.ASSIGNEE, taskEntity.getAssignee());
            putIfNotNull(data, HistoryJsonConstants.PROCESS_INSTANCE_ID, taskEntity.getProcessInstanceId());

            if (taskEntity.getExecutionId() != null) {
                ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(taskEntity.getExecutionId());
                putIfNotNull(data, HistoryJsonConstants.EXECUTION_ID, executionEntity.getId());
                String activityId = getActivityIdForExecution(executionEntity);
                putIfNotNull(data, HistoryJsonConstants.ACTIVITY_ID, activityId);
                putIfNotNull(data, HistoryJsonConstants.RUNTIME_ACTIVITY_INSTANCE_ID, activityInstanceId);

                if (isHistoryLevelAtLeast(HistoryLevel.AUDIT, taskEntity.getProcessDefinitionId())) {
                    ObjectNode activityStartData = getActivityStart(executionEntity.getId(), activityId, false);
                    if (activityStartData != null) {
                        putIfNotNull(activityStartData, HistoryJsonConstants.ASSIGNEE, taskEntity.getAssignee());
                        data.put(HistoryJsonConstants.ACTIVITY_ASSIGNEE_HANDLED, String.valueOf(true));
                    }

                } else {
                    data.put(HistoryJsonConstants.ACTIVITY_ASSIGNEE_HANDLED, String.valueOf(true));
                }
            }

            if (getHistoryConfigurationSettings().isHistoryEnabledForUserTask(taskEntity)) {
                putIfNotNull(data, HistoryJsonConstants.ID, taskEntity.getId());
                putIfNotNull(data, HistoryJsonConstants.CREATE_TIME, changeTime);
                getAsyncHistorySession().addHistoricData(getJobServiceConfiguration(), HistoryJsonConstants.TYPE_TASK_ASSIGNEE_CHANGED, data);
            }
        }
    }
}
