package com.example.integration.controller;

import org.flowable.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flowable")
public class FlowableTestController {

    @Autowired
    private ProcessEngine engine;

    @GetMapping("/engine")
    public String getEngine(){
        return engine.getName();
    }
}
