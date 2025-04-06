package com.bpm.example.demo1.validation.validator;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FieldExtension;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.TaskWithFieldExtensions;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.impl.ServiceTaskValidator;

import java.util.List;

public class CustomServiceTaskValidator extends ServiceTaskValidator {
    //校验服务任务
    @Override
    protected void verifyType(Process process, ServiceTask serviceTask,
                              List<ValidationError> errors) {
        if (StringUtils.isNotEmpty(serviceTask.getType())) {
            if (serviceTask.getType().equalsIgnoreCase("mq")) {
                validateFieldDeclarationsForMq(process, serviceTask,
                        serviceTask.getFieldExtensions(), errors);
            } else {
                super.verifyType(process, serviceTask, errors);
            }
        }
    }

    //校验MqTask类型的服务任务
    private void validateFieldDeclarationsForMq(Process process,
                                                TaskWithFieldExtensions task,
                                                List<FieldExtension> fieldExtensions,
                                                List<ValidationError> errors) {
        boolean brokerURLDefined = false;
        boolean activeQueueDefined = false;
        boolean messageTextDefined = false;

        for (FieldExtension fieldExtension : fieldExtensions) {
            if (fieldExtension.getFieldName().equals("brokerURL")) {
                brokerURLDefined = true;
            }
            if (fieldExtension.getFieldName().equals("activeQueue")) {
                activeQueueDefined = true;
            }
            if (fieldExtension.getFieldName().equals("messageText")) {
                messageTextDefined = true;
            }
        }
        if (!brokerURLDefined) {
            addError(errors, "flowable-mqtask-no-brokerURL", process, task,
                    "Mq节点没有配置brokerURL属性");
        }
        if (!activeQueueDefined) {
            addError(errors, "flowable-mqtask-no-activeQueue", process, task,
                    "Mq节点没有配置activeQueue属性");
        }
        if (!messageTextDefined) {
            addError(errors, "flowable-mqtask-no-messageText", process, task,
                    "Mq节点没有配置messageText属性");
        }
    }
}
