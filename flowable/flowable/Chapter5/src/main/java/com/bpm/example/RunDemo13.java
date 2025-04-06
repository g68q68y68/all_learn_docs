package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.persistence.entity.CommentEntity;
import org.flowable.engine.impl.persistence.entity.CommentEntityImpl;
import org.flowable.engine.impl.persistence.entity.CommentEntityManager;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;

import java.io.*;
import java.util.List;

@Slf4j
public class RunDemo13 extends FlowableEngineUtil {

    public static void main(String[] args) throws IOException {
        RunDemo13 demo = new RunDemo13();
        demo.runDemo();
    }

    private void runDemo() throws IOException {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess.bpmn20.xml");
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceById(processDefinition.getId());

        //评论测试
        Task task = taskService.createTaskQuery().processInstanceId(
                processInstance.getId()).singleResult();

        taskService.addComment(task.getId(), task.getProcessInstanceId(),
                "任务【" + task.getName() + "】:添加评论");

        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(
                processInstance.getId()).singleResult();

        taskService.addComment(task.getId(), task.getProcessInstanceId(),
                "任务【" + task.getName() + "】:添加评论");

        List<Comment> taskComments =
                taskService.getProcessInstanceComments(task.getProcessInstanceId());
        for (Comment taskComment : taskComments) {
            log.info(taskComment.getFullMessage());
        }
        taskService.deleteComments(null, task.getProcessInstanceId());
        taskComments =
                taskService.getProcessInstanceComments(task.getProcessInstanceId());
        if (taskComments.size() < 1) {
            log.info("该流程实例已经没有评论了");
        }

        //附件测试
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                "processes/SimpleProcess.bpmn20.xml");
        taskService.createAttachment("", task.getId(),
                task.getProcessInstanceId(),
                "测试附件",
                "测试附件描述",
                inputStream);

        List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
        for (Attachment taskAttachment : taskAttachments) {
            log.info(taskAttachment.getName());
            InputStream attachmentContent =
                    taskService.getAttachmentContent(taskAttachment.getId());
            log.info("附件字节数：{}", attachmentContent.available());
        }
    }
}
