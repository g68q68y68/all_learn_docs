package com.example.gateway.filters;

import com.example.gateway.config.EngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
public class EngineRouterGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private EngineConfig engineConfig;

    @Resource(name = "pikaTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private static final String PIKA_PREFIX_PROCESS_ID = "BPM#ENGINE#PROCESSID";

    private static final String PIKA_PREFIX_TASK_ID = "BPM#ENGINE#TASKID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        if (url == null || !"er".equals(url.getScheme())) {
            return chain.filter(exchange);
        }
        String id = matchId(url.getPath());
        if (StringUtils.hasText(id)) {
            String prefix = url.getHost().equals("task") ? PIKA_PREFIX_TASK_ID : PIKA_PREFIX_PROCESS_ID;
            String engineName = String.valueOf(redisTemplate.opsForHash().get(prefix, id));
            for (EngineConfig.EngineInfo engine : engineConfig.getEngines()) {
                if (engine.getName().equals(engineName)) {
                    URI requestUri = UriComponentsBuilder
                            .fromUriString(engine.getUrl())
                            .path(url.getPath())
                            .build().toUri();
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUri);
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 10100;
    }

    private String matchId(String path) {
        for (String regex : engineConfig.getPattens()) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return "";
    }
}
