package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.flowable.idm.api.GroupQuery;
import org.junit.Test;

public class RunCountGroupsDemo extends FlowableEngineUtil {
    @Test
    public void runCountGroupsDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        //创建GroupQuery
        GroupQuery groupQuery  = identityService.createGroupQuery().groupType("department");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //统计用户组数量
        groupUtil.executeCount(groupQuery);
    }
}