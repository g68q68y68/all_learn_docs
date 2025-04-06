package com.example.demo.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.history.async.HistoryJsonConstants;
import org.flowable.job.api.JobInfo;
import org.flowable.job.service.impl.asyncexecutor.message.AbstractMessageBasedJobManager;
import org.flowable.job.service.impl.history.async.transformer.HistoryJsonTransformer;
import org.flowable.job.service.impl.history.async.util.AsyncHistoryJsonUtil;
import org.flowable.job.service.impl.persistence.entity.HistoryJobEntityImpl;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class RocketMQMessageBasedJobManager extends AbstractMessageBasedJobManager {
    public final static String JOB_HISTORY_TOPIC = "job_history_topic";
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private ProcessEngineConfiguration configuration;

    @Override
    protected void sendMessage(JobInfo job) {
        if (job instanceof HistoryJobEntityImpl) {
            HistoryJobEntityImpl historyJob = (HistoryJobEntityImpl) job;
            String historicalData = historyJob.getAdvancedJobHandlerConfiguration();
            ObjectMapper objectMapper = configuration.getObjectMapper();
            JsonNode historyNode;
            String processInstanceId = "";
            try {
                historyNode = objectMapper.readTree(historicalData);
                if (historyNode instanceof ArrayNode) {
                    ArrayNode arrayNode = (ArrayNode) historyNode;
                    for (JsonNode jsonNode : arrayNode) {
                        processInstanceId = getProcessInstanceId(jsonNode);
                        if (StringUtils.isNotEmpty(processInstanceId)) {
                            break;
                        }
                    }
                } else {
                    processInstanceId = getProcessInstanceId(historyNode);
                }
            } catch (Exception e) {
                throw new FlowableException("Could not deserialize async " +
                        "history json for job (id=" + job.getId() + ")", e);
            }
            log.info("Send history data to MQ: {}", historicalData);
            JobMessage jobMessage = new JobMessage(processInstanceId,
                    historyJob.getId());
            rocketMQTemplate.syncSendOrderly(JOB_HISTORY_TOPIC, jobMessage,
                    processInstanceId);
        }
    }

    private String getProcessInstanceId(JsonNode jsonNode) {
        ObjectNode jsonData = (ObjectNode) jsonNode
                .get(HistoryJsonTransformer.FIELD_NAME_DATA);
        String processInstanceId = AsyncHistoryJsonUtil.getStringFromJson(jsonData,
                HistoryJsonConstants.PROCESS_INSTANCE_ID);
        return processInstanceId == null ? "" : processInstanceId;
    }
}

