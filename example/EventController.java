package com.nanfeng.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventListenerService eventListenerService;

    @Autowired
    public EventController(EventListenerService eventListenerService) {
        this.eventListenerService = eventListenerService;
    }

    // 启动事件监听
    @PostMapping("/start")
    public String startListening() {
        eventListenerService.initializeGUI();
        eventListenerService.startListening();
        return "事件监听已启动";
    }

    // 停止事件监听
    @PostMapping("/stop")
    public String stopListening() {
        eventListenerService.stopListening();
        return "事件监听已停止";
    }
}
