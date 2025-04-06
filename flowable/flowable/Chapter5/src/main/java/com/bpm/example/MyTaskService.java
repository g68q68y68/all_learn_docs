package com.bpm.example;

import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTaskService {
    private TaskService taskService;

    public MyTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void complete(String taskId, String userId) {
        System.out.println(userId + "尝试办理任务(ID=" + taskId + ")...");
        Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (curTask == null || curTask.getAssignee() != null) {
            System.out.println("--任务不存在或已被其他候选人办理完成");
            return;
        }

        //查找用户与任务的关系
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);

        IdentityLink userIdentityLink = null;
        for (IdentityLink identityLink : identityLinks) {
            if (identityLink.getUserId().equals(userId)) {
                userIdentityLink = identityLink;
                break;
            }
        }
        if (userIdentityLink == null) {
            System.out.println("--" + userId + "没有权限查看任务(ID=" + taskId + ")");
        } else {
            System.out.println("--" + userId + "是任务的" + userIdentityLink.getType());
            if (userIdentityLink.getType().equals("candidate")) {
                //认领任务
                taskService.claim(taskId, userId);
                //设置审批意见
                Map<String, Object> variables = new HashMap<>();
                variables.put("task_审批_outcome", "agree");
                taskService.complete(taskId, variables);
                System.out.println("----" + userId + "完成了任务(ID=" + taskId + ")的办理");
            } else {
                System.out.println("----" + userId + "没有权限办理任务(ID=" + taskId + ")");
            }
        }
    }
}

