package com.bpm.example.demo5.validation.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ProcessLevelValidator;

import java.util.List;

public class CustomValidator extends ProcessLevelValidator {
    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process,
                                     List<ValidationError> errors) {
        //查询流程中的所有UserTask节点
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        if (!CollectionUtils.isEmpty(userTasks)) {
            for (UserTask userTask : userTasks) {
                //查询办理人配置
                String assignee = userTask.getAssignee();
                //查询候选人配置
                List<String> candidateUsers = userTask.getCandidateUsers();
                //查询候选组配置
                List<String> candidateGroups = userTask.getCandidateGroups();
                //如果办理人、候选人、候选组全部没有配置
                if (StringUtils.isBlank(assignee)
                        && CollectionUtils.isEmpty(candidateUsers)
                        && CollectionUtils.isEmpty(candidateGroups)
                        ) {
                    //记录校验错误信息
                    this.addError(errors, "用户任务【" + userTask.getName() +
                            "】办理人、候选人、候选组不能全为空", process, userTask,
                            "用户任务节点办理人、候选人、候选组不能全为空");
                }
            }
        }
    }
}
