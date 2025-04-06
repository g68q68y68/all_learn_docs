package com.bpm.example.demo6.deploy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.bpm.example.demo6.annotation.TenantAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.List;

@Slf4j
public class MultiTenantDeployUtil {
    //根据租户部署流程定义
    @TenantAnnotation(tenantId = "#tenantId")
    public void deployByTenant(RepositoryService repositoryService, String tenantId,
                               String key, String resource) {
        //部署流程
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resource).key(key).tenantId(tenantId).deploy();
        //查询流程定义
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        log.info("version:{},id:{}, key:{},tenantId:{}", procDef.getVersion(),
                procDef.getId(), procDef.getKey(), procDef.getTenantId());
    }

    //根据租户查询流程定义
    @TenantAnnotation(tenantId = "#tenantId")
    public void queryProcessDefinitionFromTenant(RepositoryService repositoryService,
                                                 String tenantId, String key) {
        List processDefList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionTenantId(tenantId)
                .processDefinitionKey(key)
                .list();
        SimplePropertyPreFilter processDefFilter
                = new SimplePropertyPreFilter(ProcessDefinition.class, "version", "id",
                "key", "tenantId");
        log.info("业务方{}下的流程定义有:{}",
                tenantId, JSON.toJSONString(processDefList, processDefFilter));
    }
}
