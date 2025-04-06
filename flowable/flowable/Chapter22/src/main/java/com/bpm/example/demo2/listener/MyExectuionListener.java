package com.bpm.example.demo2.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

@Slf4j
public class MyExectuionListener extends ListenerUtil implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        //获取当前节点
        FlowElement currentFlowElement = execution.getCurrentFlowElement();
        if (execution.getEventName().equals("start")) {
            //查询开始节点自定义属性title配置的内容
            String title = super.getExtensionElementValue(currentFlowElement, "title");
            //计算表达式获取最终的值
            String titleValue = super.getExpressValue(title, execution);
            //设置流程标题
            procEngineConf.getRuntimeService().setProcessInstanceName(execution
                    .getProcessInstanceId(), titleValue);
            //设置流程变量
            execution.setVariable("title", titleValue);
        } else if (execution.getEventName().equals("end")) {
            //获取结束节点自定义属性endNoticeStarter的值
            String endNoticeStarter = super.getExtensionAttributeValue(currentFlowElement,
                    "endNoticeStarter");
            if (StringUtils.isNotBlank(endNoticeStarter) && Boolean.parseBoolean
                    (endNoticeStarter)) {
                //获取用户任务自定义属性noticeContent配置的内容
                String noticeContent = super.getExtensionElementValue(currentFlowElement,
                        "noticeContent");
                //计算表达式获取最终的值
                String noticeContentValue = super.getExpressValue(noticeContent, execution);
                log.info("发送流程结束通知：{}", noticeContentValue);
            }
        }
    }
}
