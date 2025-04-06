package com.example.gateway.router;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class RouterConfigListener {
    private Logger log = LoggerFactory.getLogger(RouterConfigListener.class);
    @Autowired
    private DynamicEngineRouteService dynamicEngineRouteService;
    @Autowired
    private NacosConfigManager nacosConfigManager;
    @Value("${router.config.dataId:router-definition.json}")
    private String dataId;
    @Value("${spring.cloud.nacos.config.group:bpm-gateway}")
    private String group;

    private final static Set<String> ROUTER_SET = new HashSet<>();

    @PostConstruct
    public void dynamicRouteListener() throws NacosException {
        nacosConfigManager.getConfigService().addListener(dataId, group, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    List<RouteDefinition> definitions = mapper.readValue(configInfo, new TypeReference<List<RouteDefinition>>() {
                    });
                    Set<String> newRouterIds = definitions.stream()
                            .map(definition -> definition.getId())
                            .collect(Collectors.toSet());

                    Iterator<String> iterator = ROUTER_SET.iterator();
                    while (iterator.hasNext()) {
                        String next = iterator.next();
                        if (!newRouterIds.contains(next)) {
                            dynamicEngineRouteService.deleteRouteDefinition(next);
                        }
                        iterator.remove();
                    }

                    for (RouteDefinition definition : definitions) {
                        if (ROUTER_SET.contains(definition.getId())) {
                            dynamicEngineRouteService.updateRouteDefinition(definition);
                        } else {
                            ROUTER_SET.add(definition.getId());
                            dynamicEngineRouteService.addRouteDefinition(definition);
                        }
                    }
                    log.info("路由更新完成------------------------");
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException e", e);
                }
            }
        });
    }


    /**
     * 启动时加载路由信息
     *
     * @throws NacosException
     * @throws JsonProcessingException
     */
    @PostConstruct
    public void init() throws NacosException, JsonProcessingException {
        String configInfo = nacosConfigManager
                .getConfigService()
                .getConfig(dataId, group, 10000);
        ObjectMapper mapper = new ObjectMapper();
        List<RouteDefinition> definitions = mapper.readValue(configInfo, new TypeReference<List<RouteDefinition>>() {
        });
        for (RouteDefinition definition : definitions) {
            ROUTER_SET.add(definition.getId());
            dynamicEngineRouteService.addRouteDefinition(definition);
        }
        log.info("路由初始化完成------------------------");
    }
}
