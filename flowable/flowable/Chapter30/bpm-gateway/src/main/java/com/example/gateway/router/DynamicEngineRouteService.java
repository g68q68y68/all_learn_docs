package com.example.gateway.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DynamicEngineRouteService implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    public void addRouteDefinition(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    public void updateRouteDefinition(RouteDefinition definition) {
        routeDefinitionWriter.delete(Mono.just(definition.getId())).subscribe();
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    public void deleteRouteDefinition(String id) {
        routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
