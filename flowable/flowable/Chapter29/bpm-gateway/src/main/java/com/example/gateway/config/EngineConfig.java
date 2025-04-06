package com.example.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "engine-config")
public class EngineConfig {
    private List<EngineInfo> engines;
    private List<String> pattens;

    public List<String> getPattens() {
        return pattens;
    }

    public void setPattens(List<String> pattens) {
        this.pattens = pattens;
    }

    public List<EngineInfo> getEngines() {
        return engines;
    }

    public void setEngines(List<EngineInfo> engines) {
        this.engines = engines;
    }

    public static class EngineInfo{
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
