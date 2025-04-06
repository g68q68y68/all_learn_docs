package com.bpm.example.demo1.bpmn.parser.factory;

import com.bpm.example.demo1.bpmn.behavior.MqTaskActivityBehavior;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.engine.impl.bpmn.helper.ClassDelegate;
import org.flowable.engine.impl.bpmn.parser.FieldDeclaration;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import java.util.List;

//自定义流程元素ActivityBehavior工厂
public class CustomActivityBehaviorFactory extends DefaultActivityBehaviorFactory {
    //创建自定义ActivityBehavior
    public MqTaskActivityBehavior createMqActivityBehavior(ServiceTask serviceTask){
        List<FieldDeclaration> fieldDeclarations = super
                .createFieldDeclarations(serviceTask.getFieldExtensions());
        return (MqTaskActivityBehavior) ClassDelegate.defaultInstantiateDelegate(
                MqTaskActivityBehavior.class, fieldDeclarations);
    }
}
