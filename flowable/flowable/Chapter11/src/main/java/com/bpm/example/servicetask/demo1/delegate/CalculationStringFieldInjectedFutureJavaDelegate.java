package com.bpm.example.servicetask.demo1.delegate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.async.AsyncTaskInvoker;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.FutureJavaDelegate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Setter
public class CalculationStringFieldInjectedFutureJavaDelegate implements FutureJavaDelegate {
    private Expression unitPrice;
    private Expression quantity;
    private Expression description;

    @Override
    public CompletableFuture execute(DelegateExecution execution, AsyncTaskInvoker taskInvoker) {
        //读取注入的unitPrice、quantity和description字段值
        double unitPriceNum = Double.valueOf((String) unitPrice.getValue(execution));
        int quantityNum = Integer.valueOf((String) quantity.getValue(execution));
        String descriptionStr = (String) description.getValue(execution);
        return taskInvoker.submit(()->{
            double totalAmount = unitPriceNum * quantityNum;
            log.info("单价：{}，数量：{}，总价：{}", unitPriceNum, quantityNum, totalAmount);
            log.info("描述信息：{}", descriptionStr);
            return totalAmount;
        });
    }

    @Override
    public void afterExecution(DelegateExecution execution, Object executionData) {
        //将异步返回结果放入流程变量totalAmount中
        execution.setVariable("totalAmount", executionData);
    }
}
