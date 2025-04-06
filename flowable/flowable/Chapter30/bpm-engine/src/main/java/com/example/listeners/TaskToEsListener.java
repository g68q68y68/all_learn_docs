package com.example.listeners;

import com.example.es.TaskDoc;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.flowable.common.engine.api.delegate.event.FlowableEngineEventType.*;

@Component
@Slf4j
public class TaskToEsListener extends AbstractFlowableEventListener {
    @Value("${bpm.engine-name}")
    private String engineName;
    @Value("${es.task-index:bpm_task}")
    private String taskIndex;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Override
    public void onEvent(FlowableEvent event) {
        TaskEntity taskEntity = (TaskEntity) ((FlowableEntityEventImpl) event).getEntity();
        if (event.getType() == TASK_CREATED || event.getType() == TASK_ASSIGNED) {
            TaskDoc taskDoc = toTaskDoc(taskEntity);
            execute(() -> elasticsearchRestTemplate.save(taskDoc));
        } else if (event.getType() == TASK_COMPLETED) {
            Document document = Document.create();
            document.put("status", 2);
            document.put("completedTime", new Date());
            UpdateQuery updateQuery = UpdateQuery.builder(taskEntity.getId())
                    .withDocument(document)
                    .build();
            execute(() -> elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of(taskIndex)));
        }
    }

    private void execute(Runnable runnable) {
        //事务提交后再写入ElasticSearch
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                executorService.submit(runnable);
            }
        });
    }

    private TaskDoc toTaskDoc(TaskEntity taskEntity) {
        TaskDoc doc = new TaskDoc();
        doc.setId(taskEntity.getId());
        doc.setName(taskEntity.getName());
        doc.setActivityId(taskEntity.getTaskDefinitionKey());
        doc.setAssignee(taskEntity.getAssignee());
        if (taskEntity.getAssignee() == null && taskEntity.getCandidates() != null) {
            String[] candidates = taskEntity.getCandidates().toArray(new String[taskEntity.getCandidates().size()]);
            doc.setCandidates(candidates);
        }
        doc.setProcessInstanceId(taskEntity.getProcessInstanceId());
        HistoricProcessInstanceEntity processInstance = CommandContextUtil
                .getHistoricProcessInstanceEntityManager()
                .findById(taskEntity.getProcessInstanceId());
        if (StringUtils.hasText(processInstance.getName())) {
            doc.setProcessInstanceName(processInstance.getName());
        } else {
            doc.setProcessInstanceName(processInstance.getProcessDefinitionName());
        }
        doc.setEngine(engineName);
        doc.setStatus(1);
        doc.setCreatedTime(taskEntity.getCreateTime());
        return doc;
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
