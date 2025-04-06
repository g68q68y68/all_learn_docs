package com.example.demo.extend;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.flowable.common.engine.impl.persistence.entity.Entity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class HistoricDataCompensationProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    public static final String HISTORIC_TOPIC = "historic_mq_topic";

    public void sendJobMessage(OpType opType, HistoricProcessInstanceEntity entity) {
        log.info("Producer historic variable message,processInstanceId={},variable={}", entity);
        HistoricProcessInstanceEntityMessage entityMessage = new HistoricProcessInstanceEntityMessage(
                opType, entity, System.currentTimeMillis());
        Message<HistoricProcessInstanceEntityMessage> message = MessageBuilder
                .withPayload(entityMessage)
                .build();
        rocketMQTemplate.syncSend(HISTORIC_TOPIC, message, 1000, 4);
    }
}

