package com.bpm.example.demo3.parser.factory;

import com.bpm.example.demo3.behavior.CustomUserTaskActivityBehavior;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

public class CustomActivityBehaviorFactory extends DefaultActivityBehaviorFactory {
    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new CustomUserTaskActivityBehavior(userTask);
    }
}

