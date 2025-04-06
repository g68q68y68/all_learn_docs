package com.example.springboot.stater.controller;

import com.example.springboot.starter.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private HelloService helloService;

    @GetMapping("/springboot/starter/hello")
    public String sayHello(String name){
       return helloService.sayHello(name);
    }

}
