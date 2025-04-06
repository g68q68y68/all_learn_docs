package com.bpm.example.demo6.multidatasource;

import lombok.Getter;
import lombok.Setter;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.multitenant
        .MultiSchemaMultiTenantProcessEngineConfiguration;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantDataSourceProcessEngineConfiguration
        extends MultiSchemaMultiTenantProcessEngineConfiguration {
    @Setter
    @Getter
    private Map<String, DataSource> tenantDataSourceRelationMap = new ConcurrentHashMap<>();

    public MultiTenantDataSourceProcessEngineConfiguration(TenantInfoHolder tenantInfoHolder) {
        super(tenantInfoHolder);
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        //绑定租户和数据源
        for (String tenantId : tenantInfoHolder.getAllTenants()) {
            super.registerTenant(tenantId, tenantDataSourceRelationMap.get(tenantId));
        }
        return super.buildProcessEngine();
    }
}
