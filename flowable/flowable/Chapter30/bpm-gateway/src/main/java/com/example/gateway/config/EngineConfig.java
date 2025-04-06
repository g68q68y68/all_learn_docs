package com.example.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "engine-config")
@Data
public class EngineConfig {
    private List<EngineInfo> engines;
    private List<String> pattens;
    @Data
    public static class EngineInfo{
        private String name;
        private String url;
    }
}
