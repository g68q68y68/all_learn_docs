package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.flowable.idm.api.GroupQuery;
import org.junit.Test;

public class RunSingleResultGroupsDemo extends FlowableEngineUtil {
    @Test
    public void runSingleResultGroupsDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //创建GroupQuery
        GroupQuery groupQuery = identityService.createGroupQuery()
                .groupId("process_platform_department").groupType("department");
        //查询单个用户组
        groupUtil.executeSingleResult(groupQuery);
    }
}