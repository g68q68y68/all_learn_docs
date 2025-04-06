package com.example.demo.controller;

import com.example.demo.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ProcessDefinitionController {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/getProcessDefinitions")
    public List<String> getProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().listPage(0, 20);
        RedisTemplate<String, String> redisClient = SpringUtil.getBean(RedisTemplate.class);
        System.out.println(redisClient);
        return processDefinitions.stream().map(p->p.getId()).collect(Collectors.toList());
    }


    @GetMapping("/process/deploy")
    public Deployment deploy() throws FileNotFoundException {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("leave.bpmn20.xml")
                .disableSchemaValidation()
                .deploy();

        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        log.info("Found process definition [{}] with id [{}]", processDefinition.getName(), processDefinition.getId());
        return deployment;
    }
}
