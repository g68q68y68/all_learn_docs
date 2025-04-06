package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Map<String, Object> sayHello() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("msg", "success");
        return ret;
    }
}