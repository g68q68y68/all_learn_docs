package com.bpm.example.servicetask.demo4.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.DelegateHelper;
import org.flowable.engine.impl.delegate.ActivityBehavior;

@Slf4j
public class InventoryCheckingActivityBehavior implements ActivityBehavior {
    //初始化库存
    int storage = 5;

    @Override
    public void execute(DelegateExecution execution) {
        int applyNum = (int)execution.getVariable("applyNum");
        String description = (String)execution.getVariable("description");
        log.info("申请领用数量：{}", applyNum);
        log.info("申请原因：{}", description);
        String sequenceFlowToTake = "noExceptionSequenceFlow";
        try {
            //执行库存校验逻辑
            checkInventory(applyNum);
        } catch (Exception e) {
            sequenceFlowToTake = "exceptionSequenceFlow";
        }
        //控制流程流向
        DelegateHelper.leaveDelegate(execution, sequenceFlowToTake);
    }

    /**
     * 库存校验接口，库存不足时抛出异常
     * @param applyNum  物品领用数量
     * @throws Exception
     */
    public void checkInventory(int applyNum) throws Exception{
        if (applyNum > storage) {
            log.error("库存数量为：{}，库存不足！", storage);
            throw new Exception("库存不足！");
        }
    }
}
