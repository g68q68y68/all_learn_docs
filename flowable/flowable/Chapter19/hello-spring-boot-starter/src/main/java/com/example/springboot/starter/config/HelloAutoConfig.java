package com.example.springboot.starter.config;

import com.example.springboot.starter.service.HelloService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "hello", name = "msg")
public class HelloAutoConfig {

    @Value("${hello.msg}")
    private String msg;

    @ConditionalOnMissingBean(HelloService.class)
    @Bean
    public HelloService helloService() {
        return new HelloService(msg);
    }
}
