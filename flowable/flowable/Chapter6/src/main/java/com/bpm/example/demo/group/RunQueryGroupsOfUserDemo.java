package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.flowable.idm.api.GroupQuery;
import org.junit.Test;

public class RunQueryGroupsOfUserDemo extends FlowableEngineUtil {
    @Test
    public void runQueryGroupsOfUserDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //查询用户所在的用户组列表
        GroupQuery groupQuery = identityService.createGroupQuery()
                .groupMember("hebo").orderByGroupId().asc();
        groupUtil.executeList(groupQuery);
    }
}
