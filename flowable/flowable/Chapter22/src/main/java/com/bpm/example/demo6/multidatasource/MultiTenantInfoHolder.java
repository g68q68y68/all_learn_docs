package com.bpm.example.demo6.multidatasource;

import lombok.Getter;
import lombok.Setter;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiTenantInfoHolder implements TenantInfoHolder {
    @Setter
    @Getter
    private CopyOnWriteArrayList<String> tenantList = new CopyOnWriteArrayList<>();

    private static ThreadLocal<String> tenantThreadLocal = new ThreadLocal();

    @Override
    public Collection<String> getAllTenants() {
        return tenantList;
    }

    @Override
    public void setCurrentTenantId(String tenantId) {
        tenantThreadLocal.set(tenantId);
    }

    @Override
    public String getCurrentTenantId() {
        return tenantThreadLocal.get();
    }

    @Override
    public void clearCurrentTenantId() {
        tenantThreadLocal.remove();
    }
}
