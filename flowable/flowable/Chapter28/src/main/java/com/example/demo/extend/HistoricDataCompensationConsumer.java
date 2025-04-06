package com.example.demo.extend;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.persistence.entity.data.HistoricProcessInstanceDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = HistoricDataCompensationProducer.HISTORIC_TOPIC,
        consumerGroup = "historic_compensation_consumer_group"
)
@Slf4j
public class HistoricDataCompensationConsumer implements RocketMQListener<HistoricProcessInstanceEntityMessage> {
    @Autowired
    private ProcessEngineConfigurationImpl configuration;

    @Override
    public void onMessage(HistoricProcessInstanceEntityMessage entityMessage) {
        HistoricProcessInstanceDataManager processInstanceDataManager =
                configuration.getHistoricProcessInstanceDataManager();
        HistoricProcessInstanceEntity entity = processInstanceDataManager
                .findById(entityMessage.getEntity().getId());
        switch (entityMessage.getOpType()) {
            case INSERT:
                if (entity == null) {
                    processInstanceDataManager.insert(entityMessage.getEntity());
                }
                break;
            case UPDATE:
                if (entity != null && entity.getRevision() <= entityMessage.getEntity().getRevision()) {
                    processInstanceDataManager.update(entityMessage.getEntity());
                }
                break;
            case DELETE:
                if (entity != null && entity.getRevision() <= entityMessage.getEntity().getRevision()) {
                    processInstanceDataManager.delete(entityMessage.getEntity());
                }
                break;
            default:
                log.error("Unsupported opType.message={}", entityMessage);
        }
    }
}

