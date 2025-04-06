package com.example.demo.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.flowable.engine.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = RocketMQMessageBasedJobManager.JOB_HISTORY_TOPIC,
        consumerGroup = "job_consumer_group",
        consumeMode = ConsumeMode.ORDERLY
)
@Slf4j
public class HistoricConsumer implements RocketMQListener<JobMessage> {
    @Autowired
    private ManagementService managementService;
    @Override
    public void onMessage(JobMessage jobMessage) {
        log.info("Consumer job message {}", jobMessage);
        managementService.executeHistoryJob(jobMessage.getJobId());
    }
}
