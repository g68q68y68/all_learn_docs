package com.example.demo.extend;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.HistoricProcessInstanceQueryImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.flowable.engine.impl.persistence.entity.data.AbstractProcessDataManager;
import org.flowable.engine.impl.persistence.entity.data.HistoricProcessInstanceDataManager;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MongoHistoricProcessInstanceDataManager extends AbstractProcessDataManager<HistoricProcessInstanceEntity> implements HistoricProcessInstanceDataManager {

    private MongoTemplate mongoTemplate;

    private HistoricDataCompensationProducer historicDataProducer;

    public MongoHistoricProcessInstanceDataManager(ProcessEngineConfigurationImpl processEngineConfiguration, MongoTemplate mongoTemplate) {
        super(processEngineConfiguration);
        this.mongoTemplate = mongoTemplate;
    }

    //根据ID查询历史流程实例数据
    public HistoricProcessInstanceEntity findById(String entityId) {
        MongoHistoricProcessInstanceEntityImpl entity = mongoTemplate
                .findById(entityId, MongoHistoricProcessInstanceEntityImpl.class);
        return entity;
    }

    //插入历史流程实例
    public void insert(HistoricProcessInstanceEntity entity) {
        log.info("Insert HistoricProcessInstanceEntity {}", entity);
        executeWithCompensation(() -> mongoTemplate.insert(entity), entity, OpType.INSERT);
    }

    //更新历史流程实例
    public HistoricProcessInstanceEntity update(HistoricProcessInstanceEntity entity) {
        executeWithCompensation(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(entity.getId()));
            Document document = (Document) mongoTemplate.getConverter()
                    .convertToMongoType(entity);
            Update update = Update.fromDocument(document);
            mongoTemplate.updateFirst(query, update,
                    MongoHistoricProcessInstanceEntityImpl.class);
        }, entity, OpType.UPDATE);
        return entity;
    }

    //根据实体对象删除流程历史
    public void delete(HistoricProcessInstanceEntity entity) {
        executeWithCompensation(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(entity.getId()));
            mongoTemplate.remove(query, MongoHistoricProcessInstanceEntityImpl.class);
        }, entity, OpType.DELETE);
    }

    private void executeWithCompensation(Runnable runnable,
                                         HistoricProcessInstanceEntity entity,
                                         OpType opType) {
        try {
            runnable.run();
        } catch (Exception ex) {
            historicDataProducer.sendJobMessage(opType, entity);
        }
    }

    //根据ID删除流程历史
    public void delete(String id) {
        HistoricProcessInstanceEntity entity = new MongoHistoricProcessInstanceEntityImpl();
        entity.setId(id);
        delete(entity);
    }

    //根据流程定义ID查询历史流程实例
    @Override
    public List<String> findHistoricProcessInstanceIdsByProcessDefinitionId(String processDefinitionId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("processDefinitionId").is(processDefinitionId));
        query.fields().include("processInstanceId");
        List<MongoHistoricProcessInstanceEntityImpl> list = mongoTemplate.find(query, MongoHistoricProcessInstanceEntityImpl.class);
        return list.stream().map(entity -> entity.getProcessInstanceId()).collect(Collectors.toList());
    }

    //根据父流程实例ID查询历史流程实例
    @Override
    public List<HistoricProcessInstance> findHistoricProcessInstancesBySuperProcessInstanceId(String superProcessInstanceId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("superProcessInstanceId").is(superProcessInstanceId));
        List<MongoHistoricProcessInstanceEntityImpl> list = mongoTemplate.find(query, MongoHistoricProcessInstanceEntityImpl.class);
        List<HistoricProcessInstance> ret = new ArrayList<>(list);
        return ret;
    }

    //根据父流程实例ID批量查询历史流程实例
    @Override
    public List<String> findHistoricProcessInstanceIdsBySuperProcessInstanceIds(Collection<String> superProcessInstanceIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("superProcessInstanceId").in(superProcessInstanceIds));
        List<MongoHistoricProcessInstanceEntityImpl> list = mongoTemplate.find(query, MongoHistoricProcessInstanceEntityImpl.class);
        return list.stream().map(entity -> entity.getProcessInstanceId()).collect(Collectors.toList());
    }

    //根据条件查询历史流程实例数量
    @Override
    public long findHistoricProcessInstanceCountByQueryCriteria(HistoricProcessInstanceQueryImpl historicProcessInstanceQuery) {
        Query query = convertQuery(historicProcessInstanceQuery);
        return mongoTemplate.count(query, MongoHistoricProcessInstanceEntityImpl.class);
    }

    //根据条件查询历史流程实例数量
    @Override
    public List<HistoricProcessInstance> findHistoricProcessInstancesByQueryCriteria(HistoricProcessInstanceQueryImpl historicProcessInstanceQuery) {
        Query query = convertQuery(historicProcessInstanceQuery);
        return new ArrayList<>(mongoTemplate.find(query, MongoHistoricProcessInstanceEntityImpl.class));
    }

    //根据条件查询历史流程实例以及关联的变量
    @Override
    public List<HistoricProcessInstance> findHistoricProcessInstancesAndVariablesByQueryCriteria(HistoricProcessInstanceQueryImpl historicProcessInstanceQuery) {
        Query query = convertQuery(historicProcessInstanceQuery);
        ArrayList<HistoricProcessInstance> historicProcessInstances = new ArrayList<>(mongoTemplate.find(query,
                MongoHistoricProcessInstanceEntityImpl.class));
        for (HistoricProcessInstance instance : historicProcessInstances) {
            List<HistoricVariableInstanceEntity> variables = getProcessEngineConfiguration()
                    .getVariableServiceConfiguration()
                    .getHistoricVariableInstanceDataManager()
                    .findHistoricVariableInstancesByProcessInstanceId(instance.getId());
            MongoHistoricProcessInstanceEntityImpl monogoInstance =
                    (MongoHistoricProcessInstanceEntityImpl) instance;
            monogoInstance.setQueryVariables(variables);
        }
        return historicProcessInstances;
    }

    @Override
    public List<HistoricProcessInstance> findHistoricProcessInstancesByNativeQuery(Map<String, Object> parameterMap) {
        throw new RuntimeException("不支持的操作");
    }

    @Override
    public long findHistoricProcessInstanceCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new RuntimeException("不支持的操作");
    }

    @Override
    public void deleteHistoricProcessInstances(HistoricProcessInstanceQueryImpl historicProcessInstanceQuery) {
        Query query = convertQuery(historicProcessInstanceQuery);
        mongoTemplate.remove(query, MongoHistoricProcessInstanceEntityImpl.class);
    }

    @Override
    public void bulkDeleteHistoricProcessInstances(Collection<String> processInstanceIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("processInstanceId").in(processInstanceIds));
        mongoTemplate.remove(query, MongoHistoricProcessInstanceEntityImpl.class);
    }

    private Query convertQuery(HistoricProcessInstanceQueryImpl instanceQuery) {
        Query query = new Query();
        if (StringUtils.isNotBlank(instanceQuery.getId())) {
            query.addCriteria(Criteria.where("_id").is(instanceQuery.getId()));
        }
        if (StringUtils.isNotBlank(instanceQuery.getProcessInstanceId())) {
            query.addCriteria(Criteria.where("processInstanceId").is(instanceQuery.getProcessInstanceId()));
        }
        if (StringUtils.isNotBlank(instanceQuery.getProcessDefinitionId())) {
            query.addCriteria(Criteria.where("processDefinitionId").is(instanceQuery.getProcessDefinitionId()));
        }
        if (StringUtils.isNotBlank(instanceQuery.getBusinessKey())) {
            query.addCriteria(Criteria.where("businessKey").is(instanceQuery.getBusinessKey()));
        }
        return query;
    }

    public MongoHistoricProcessInstanceDataManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public Class<? extends HistoricProcessInstanceEntity> getManagedEntityClass() {
        return MongoHistoricProcessInstanceEntityImpl.class;
    }

    @Override
    public HistoricProcessInstanceEntity create() {
        return new MongoHistoricProcessInstanceEntityImpl();
    }

    @Override
    public HistoricProcessInstanceEntity create(ExecutionEntity processInstanceExecutionEntity) {
        return new MongoHistoricProcessInstanceEntityImpl(processInstanceExecutionEntity);
    }
}
