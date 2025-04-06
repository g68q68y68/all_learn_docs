package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.text.SimpleDateFormat;
import java.util.List;

public class RunDemo7 extends FlowableEngineUtil {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        RunDemo7 demo = new RunDemo7();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //部署流程
        ProcessDefinition processDefinition =
                deployResource("processes/SimpleProcess4.bpmn20.xml");
        System.out.println("流程定义ID为：" + processDefinition.getId() + "，流程定义key为：" +
                processDefinition.getKey());
        //发起5个流程实例
        System.out.println("发起5个流程实例");
        for (int i = 0; i < 5; i++) {
            ProcessInstance processInstance =
                    runtimeService.startProcessInstanceById(processDefinition.getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int i = 0;
        while (completeTask()) {
            i++;
            System.out.println("～～完成待办任务处理第" + i + "次轮询！");
        }
    }

    //查询并办理任务
    private boolean completeTask() {
        List<Task> tasks1 = queryUserTodoTasks("huhaiqin");
        List<Task> tasks2 = queryUserTodoTasks("hebo");
        if (tasks1 != null && tasks1.size() != 0) {
            System.out.println("**huhaiqin开始审批任务：");
            for (Task task : tasks1) {
                taskService.complete(task.getId());
                System.out.println("  完成任务ID=" + task.getId());
            }
        }
        if (tasks2 != null && tasks2.size() != 0) {
            System.out.println("**hebo开始审批任务：");
            for (Task task : tasks2) {
                taskService.complete(task.getId());
                System.out.println("  完成任务ID=" + task.getId());
            }
        }
        return (tasks1 != null && tasks1.size() != 0) || (tasks2 != null && tasks2.size() != 0);
    }

    //查询用户待办任务
    private List<Task> queryUserTodoTasks(String userId) {
        System.out.println("=====查询" + userId + "的待办任务");
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId)
                .orderByTaskCreateTime().desc().list();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                System.out.println("  任务[" + task.getName() + "](ID=" + task.getId() + ")"
                        + "的办理人为：" + task.getAssignee() + "，创建时间为：" + dateFormat.format(task.getCreateTime())
                );
            }
        } else {
            System.out.println("  " + userId + "没有待办任务");
        }
        return taskList;
    }
}

