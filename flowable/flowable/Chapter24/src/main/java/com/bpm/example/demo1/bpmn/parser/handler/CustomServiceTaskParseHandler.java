package com.bpm.example.demo1.bpmn.parser.handler;

import com.bpm.example.demo1.bpmn.parser.factory.CustomActivityBehaviorFactory;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.flowable.engine.impl.bpmn.parser.handler.ServiceTaskParseHandler;
import org.apache.commons.lang3.StringUtils;

public class CustomServiceTaskParseHandler extends ServiceTaskParseHandler {

    @Override
    protected void executeParse(BpmnParse bpmnParse, ServiceTask serviceTask) {
        if (StringUtils.isNotEmpty(serviceTask.getType())) {
            if (serviceTask.getType().equalsIgnoreCase("mq")) {
                serviceTask.setBehavior(((CustomActivityBehaviorFactory)bpmnParse
                        .getActivityBehaviorFactory()).createMqActivityBehavior(serviceTask));
            }
        }
        super.executeParse(bpmnParse, serviceTask);
    }
}
