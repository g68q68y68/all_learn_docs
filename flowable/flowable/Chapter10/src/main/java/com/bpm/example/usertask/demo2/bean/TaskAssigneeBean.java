package com.bpm.example.usertask.demo2.bean;

import lombok.Data;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
public class TaskAssigneeBean implements Serializable {
    //指定用户办理人
    private String designatedUserName;

    //该方法将获取任务办理人
    public String getMangerOfProcessInitiator() {
        return "hebo";
    }

    //该方法将获取任务候选人
    public List<String> getCandidateUsers() {
        List<String> candidateUsers = Arrays.asList("liuxiaopeng","huhaiqin");
        return candidateUsers;
    }

    //该方法将获取任务候选用户组
    public List<String> getCandidateGroups() {
        List<String> candidateGroups = Arrays.asList("group1","group2");
        return candidateGroups;
    }
}
