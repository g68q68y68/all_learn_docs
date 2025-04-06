package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.junit.Test;

public class RunRemoveUserFromGroupDemo extends FlowableEngineUtil {
    @Test
    public void runRemoveUserFromGroupDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //将用户从用户组中移除
        groupUtil.removeUserFromGroup("zhangsan", "testgroup");
    }
}
