package com.bpm.example.demo2.cmd;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.identitylink.service.HistoricIdentityLinkService;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.util.CollectionUtils;

import java.util.List;

@AllArgsConstructor
public class TaskCarbonCopyCmd implements Command<Void> {
    private String taskId;
    private List<String> userIds;

    @Override
    public Void execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl procEngineConf = CommandContextUtil
                .getProcessEngineConfiguration(commandContext);
        HistoryService historyService = procEngineConf.getHistoryService();
        //校验并获取任务实例
        HistoricTaskInstance hisTask = checkAndGetHistoricTaskInstance(historyService);
        if (hisTask.getEndTime() == null) {
            //对未结束的任务同时创建运行时和历史关联关系
            for (String userId : userIds) {
                IdentityLinkEntity identityLink = procEngineConf
                        .getIdentityLinkServiceConfiguration()
                        .getIdentityLinkService()
                        .createTaskIdentityLink(taskId, userId, null, "carbonCopy");
                CommandContextUtil.getHistoryManager()
                        .recordIdentityLinkCreated(identityLink);
            }
        } else {
            //对于已结束的任务，仅创建历史关联关系
            HistoricIdentityLinkService hisLinkService = procEngineConf
                    .getIdentityLinkServiceConfiguration()
                    .getHistoricIdentityLinkService();
            for (String userId : userIds) {
                HistoricIdentityLinkEntity hisIdentityLink = hisLinkService
                        .createHistoricIdentityLink();
                hisIdentityLink.setGroupId(null);
                hisIdentityLink.setProcessInstanceId(null);
                hisIdentityLink.setTaskId(taskId);
                hisIdentityLink.setType("carbonCopy");
                hisIdentityLink.setUserId(userId);
                hisLinkService.insertHistoricIdentityLink(hisIdentityLink, false);
            }
        }
        return null;
    }

    /**
     * 校验并获取任务实例
     *
     * @param historyService
     * @return
     */
    private HistoricTaskInstance checkAndGetHistoricTaskInstance(HistoryService
                                                                         historyService) {
        if (StringUtils.isBlank(taskId)) {
            throw new FlowableIllegalArgumentException("taskId不能为空");
        }
        if (CollectionUtils.isEmpty(userIds)) {
            throw new FlowableIllegalArgumentException("userIds不能为空");
        }
        HistoricTaskInstance historicTaskInstance = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            throw new FlowableException("id为" + taskId + "的任务实例不存在");
        }
        return historicTaskInstance;
    }
}
