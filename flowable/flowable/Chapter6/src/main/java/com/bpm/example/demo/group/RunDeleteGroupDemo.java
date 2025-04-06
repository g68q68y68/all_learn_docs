package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.junit.Test;

public class RunDeleteGroupDemo extends FlowableEngineUtil {
    @Test
    public void runDeleteGroupDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //删除用户组
        groupUtil.deleteGroup("testGroup");
    }
}
