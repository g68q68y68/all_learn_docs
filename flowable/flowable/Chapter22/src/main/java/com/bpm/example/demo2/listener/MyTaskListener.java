package com.bpm.example.demo2.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

@Slf4j
public class MyTaskListener extends ListenerUtil implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //获取流程BpmnModel
        BpmnModel bpmnModel = procEngineConf.getRepositoryService()
                .getBpmnModel(delegateTask.getProcessDefinitionId());
        //查询用户任务的FlowElement
        FlowElement flowElement = bpmnModel
                .getFlowElement(delegateTask.getTaskDefinitionKey());
        //获取结束节点自定义属性enableToDoNotice的值
        String enableToDoNotice = super
                .getExtensionAttributeValue(flowElement , "enableToDoNotice");
        if (StringUtils.isNotBlank(enableToDoNotice) && Boolean.parseBoolean(enableToDoNotice)) {
            //查询用户任务自定义属性noticeContent配置的内容
            String noticeContent = super
                    .getExtensionElementValue(flowElement, "noticeContent");
            //计算表达式获取最终的值
            String noticeContentValue = super.getExpressValue(noticeContent, delegateTask);
            log.info("发送待办通知：{}", noticeContentValue);
        }
    }
}
