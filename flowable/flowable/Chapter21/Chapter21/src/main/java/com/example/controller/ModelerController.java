package com.example.controller;

import com.example.entity.Model;
import com.example.service.ModelService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelerController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ModelService modelService;

    @PostMapping("/model/saveModel")
    public String saveModel(@RequestBody Model model) {
        modelService.saveModel(model);
        return model.getId();
    }

    @PostMapping("/model/deploy/{modelId}")
    public String deploy(@PathVariable String modelId) {
        Model model = modelService.getModelById(modelId);
        Deployment deploy = repositoryService
                .createDeployment()
                .addString(model.getName(), model.getModelXml())
                .deploy();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploy.getId())
                .singleResult();

        return processDefinition.getId();
    }
}

