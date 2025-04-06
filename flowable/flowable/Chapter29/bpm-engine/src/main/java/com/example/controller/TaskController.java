package com.example.controller;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 根据流程实例ID查询待办任务
     * @param processInstanceId
     * @return
     */
    @GetMapping("/processInstance/{processInstanceId}")
    public ResponseEntity<List<Map<String, Object>>> queryTasks(@PathVariable("processInstanceId") String processInstanceId) {
        List<Task> taskList = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        List<Map<String, Object>> ret = new ArrayList<>();
        for (Task task : taskList) {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("taskId", task.getId());
            taskData.put("name", task.getName());
            ret.add(taskData);
        }
        return ResponseEntity.ok(ret);
    }

    /**
     * 根据任务ID办理任务
     * @param taskId
     * @param variables
     * @return
     */
    @PostMapping("/complete/{taskId}")
    public ResponseEntity<String> completeTask(@PathVariable("taskId") String taskId,
                                               Map<String, Object> variables) {
        taskService.complete(taskId, variables);
        return ResponseEntity.ok("success");
    }
}
