package com.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "process-modeling-service",url = "${service.process-modeling-service.url}")
public interface ProcessDefinitionClient {

    @PostMapping("/processDefinition/syncById/{processDefinitionId}")
    public ResponseEntity<String> syncProcessDefinition(@PathVariable("processDefinitionId") String processDefinitionId);

}
