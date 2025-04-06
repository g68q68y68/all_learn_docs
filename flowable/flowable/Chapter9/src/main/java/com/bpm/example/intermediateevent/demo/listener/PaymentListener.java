package com.bpm.example.intermediateevent.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
import org.flowable.engine.impl.context.Context;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.delegate.DelegateTask;

@Slf4j
public class PaymentListener implements TaskListener {
    private int balance = 100;

    @Override
    public void notify(DelegateTask delegateTask) {
        int applicationFee = (Integer) delegateTask.getVariable("applicationFee");
        try {
            if (applicationFee > balance) {
                log.error("余额不足，支付失败！");
                throw new BpmnError("500");
            } else {
                log.info("余额充足，支付成功！");
            }
        } catch (BpmnError error) {
            ExecutionEntity exectuion = Context.getProcessEngineConfiguration()
                    .getExecutionEntityManager()
                    .findById(delegateTask.getExecutionId());
            //抛出错误事件
            ErrorPropagation.propagateError(error, exectuion);
        }
    }
}
