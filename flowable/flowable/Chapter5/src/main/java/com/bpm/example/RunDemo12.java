package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RunDemo12 extends FlowableEngineUtil {
    public static void main(String[] args) {
        RunDemo12 demo = new RunDemo12();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //准备参数
        Date curDate = new Date();
        System.out.println("当前时间为：" + curDate);
        String express = "${dateFormat.format(date)}";
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("date", curDate);
        variableMap.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        //调用执行表达式的CMD
        Object result = managementService.executeCommand(
                new ExecuteExpressionCmd(express, variableMap));
        System.out.println("格式化后：" + result);
    }
}

