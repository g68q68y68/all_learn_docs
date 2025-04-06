package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.flowable.idm.api.GroupQuery;
import org.junit.Test;

public class ListPageGroupsDemo extends FlowableEngineUtil {
    @Test
    public void listPageGroupsTest() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        GroupQuery groupQuery = identityService.createGroupQuery()
                .groupType("department").orderByGroupId().asc();
        groupUtil.executeListPage(groupQuery,1,3);
    }
}