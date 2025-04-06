package com.bpm.example.usertask.demo3.listener;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Arrays;
import java.util.List;

public class UserTaskListener implements TaskListener {
    public void notify(DelegateTask delegateTask) {
        switch (delegateTask.getTaskDefinitionKey()){
            case "userTask1":
                //为用户任务userTask1设置办理人
                delegateTask.setAssignee("hebo");
                break;
            case "userTask2":
                //为用户任务userTask2设置候选人
                List<String> candidateUsers = Arrays.asList("liuxiaopeng","huhaiqin");
                delegateTask.addCandidateUsers(candidateUsers);
                break;
            case "userTask3":
                //为用户任务userTask3设置候选组
                List<String> candidateGroups = Arrays.asList("group1","group2");
                delegateTask.addCandidateGroups(candidateGroups);
                break;
            case "userTask4":
                //为用户任务userTask4设置办理人
                String designatedUserName
                        = (String)delegateTask.getVariable("designatedUserName");
                delegateTask.setAssignee(designatedUserName);
                break;
        }
    }
}