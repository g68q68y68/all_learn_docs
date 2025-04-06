package com.example.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoController {

    @RequestMapping("/demo/springmvc/hello")
    @ResponseBody
    public Map<String,String> getHello(){
        Map<String,String> ret = new HashMap<>();
        ret.put("msg","success");
        return ret;
    }

}
