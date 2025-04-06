package com.bpm.example.subprocess.demo3.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

@Slf4j
public class TreasuryDeductService implements JavaDelegate {
    int goodsTreasury = 10;

    @Override
    public void execute(DelegateExecution execution) {
        int goodsNum  = (Integer) execution.getVariable("goodsNum");
        if (goodsNum > goodsTreasury) {
            log.error("库存不足，订单将取消！");
            throw new BpmnError("500");
        } else {
            log.info("库存充足，订单即将发货！");
        }
    }
}
