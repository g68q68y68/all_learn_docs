package com.example.extend;

import com.example.cache.RedisProcessDefinitionCache;
import com.example.client.ProcessDefinitionClient;
import org.flowable.engine.impl.ProcessDefinitionQueryImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.flowable.engine.impl.persistence.entity.data.ProcessDefinitionDataManager;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomProcessDefinitionDataManagerImpl implements ProcessDefinitionDataManager {

    @Autowired
    private RedisProcessDefinitionCache processDefinitionCache;

    @Override
    public ProcessDefinitionEntity findById(String entityId) {
        return (ProcessDefinitionEntity) processDefinitionCache.get(entityId).getProcessDefinition();
    }

    @Override
    public ProcessDefinitionEntity findLatestProcessDefinitionByKey(String processDefinitionKey) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findLatestProcessDefinitionByKeyAndTenantId(String processDefinitionKey, String tenantId) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findLatestDerivedProcessDefinitionByKey(String processDefinitionKey) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findLatestDerivedProcessDefinitionByKeyAndTenantId(String processDefinitionKey, String tenantId) {
        return null;
    }

    @Override
    public void deleteProcessDefinitionsByDeploymentId(String deploymentId) {

    }

    @Override
    public List<ProcessDefinition> findProcessDefinitionsByQueryCriteria(ProcessDefinitionQueryImpl processDefinitionQuery) {
        return null;
    }

    @Override
    public long findProcessDefinitionCountByQueryCriteria(ProcessDefinitionQueryImpl processDefinitionQuery) {
        return 0;
    }

    @Override
    public ProcessDefinitionEntity findProcessDefinitionByDeploymentAndKey(String deploymentId, String processDefinitionKey) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findProcessDefinitionByDeploymentAndKeyAndTenantId(String deploymentId, String processDefinitionKey, String tenantId) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findProcessDefinitionByParentDeploymentAndKey(String parentDeploymentId, String processDefinitionKey) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findProcessDefinitionByParentDeploymentAndKeyAndTenantId(String parentDeploymentId, String processDefinitionKey, String tenantId) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findProcessDefinitionByKeyAndVersion(String processDefinitionKey, Integer processDefinitionVersion) {
        return null;
    }

    @Override
    public ProcessDefinitionEntity findProcessDefinitionByKeyAndVersionAndTenantId(String processDefinitionKey, Integer processDefinitionVersion, String tenantId) {
        return null;
    }

    @Override
    public List<ProcessDefinition> findProcessDefinitionsByNativeQuery(Map<String, Object> parameterMap) {
        return null;
    }

    @Override
    public long findProcessDefinitionCountByNativeQuery(Map<String, Object> parameterMap) {
        return 0;
    }

    @Override
    public void updateProcessDefinitionTenantIdForDeployment(String deploymentId, String newTenantId) {

    }

    @Override
    public void updateProcessDefinitionVersionForProcessDefinitionId(String processDefinitionId, int version) {

    }

    @Override
    public ProcessDefinitionEntity create() {
        return new ProcessDefinitionEntityImpl();
    }


    @Override
    public void insert(ProcessDefinitionEntity entity) {

    }

    @Override
    public ProcessDefinitionEntity update(ProcessDefinitionEntity entity) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void delete(ProcessDefinitionEntity entity) {

    }
}
