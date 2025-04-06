package com.example.controller;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/processInstance")
public class ProcessInstanceController {

    @Autowired
    private RuntimeService runtimeService;

    @PostMapping("/startByProcessDefinitionId/{processDefinitionId}")
    public String startProcessInstance(@PathVariable("processDefinitionId") String processDefinitionId,
                                       Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        return processInstance.getProcessInstanceId();
    }
}
