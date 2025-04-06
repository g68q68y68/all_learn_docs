package com.example.demo.controller;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ProcessTestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据流程定义ID发起流程
     * @param processDefinitionId  流程定义ID
     * @param variables 流程变量
     * @return
     */
    @PostMapping("/process/start/{processDefinitionId}")
    public Map<String, String> createProcess(@PathVariable String processDefinitionId, @RequestBody Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        Map<String, String> ret = new HashMap<>();
        ret.put("processInstanceId", processInstance.getId());
        return ret;
    }

    /**
     * 根据流程实例ID查询任务
     * @param processInstanceId 流程实例ID
     * @return
     */
    @GetMapping("/process/tasks/{processInstanceId}")
    public List<String> queryTasks(@PathVariable String processInstanceId) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return list.stream()
                .map(task -> task.getId())
                .collect(Collectors.toList());
    }

    /**
     * 根据任务ID办理任务
     * @param taskId 任务ID
     * @param variables 流程变量
     */
    @PutMapping("/task/complete/{taskId}")
    public void completeTask(@PathVariable String taskId, @RequestBody Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
}
