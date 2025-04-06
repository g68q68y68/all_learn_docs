package com.example.controller;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ProcessDefinitionController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @PostMapping("/processDefinition/syncById/{processDefinitionId}")
    public ResponseEntity<String> syncProcessDefinition(@PathVariable("processDefinitionId") String processDefinitionId) {
        //如果缓存中不存在，该方法会从数据库中重新加载流程定义到缓存中
        ProcessDefinition processDefinition = managementService.executeCommand(context -> context
                .getProcessEngineConfiguration()
                .getDeploymentManager()
                .findDeployedProcessDefinitionById(processDefinitionId));
        if (processDefinition != null) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/processDefinition/deployByClasspath/{fileName}")
    public ResponseEntity<String> deployByClassPath(@PathVariable("fileName") String fileName) {
        repositoryService.createDeployment()
                .addClasspathResource(fileName + ".bpmn20.xml")
                .deploy();
        return ResponseEntity.ok("success");
    }
}