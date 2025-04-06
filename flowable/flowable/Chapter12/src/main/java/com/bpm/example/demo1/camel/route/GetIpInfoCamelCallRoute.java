package com.bpm.example.demo1.camel.route;

import com.bpm.example.demo1.camel.processor.ResultProcessor;
import org.apache.camel.builder.RouteBuilder;

public class GetIpInfoCamelCallRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("flowable:camelTaskProcess:camelTask1?copyVariablesToProperties=true")
           .toD("http://whois.pconline.com.cn/ipJson.jsp?ip=${exchange.properties[ip]}&json=true")
           .process(new ResultProcessor());
    }
}

