package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import org.junit.Test;

public class RunAddGroupDemo extends FlowableEngineUtil {
    @Test
    public void runAddGroupDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        groupUtil.addGroup("process_platform_department", "流程平台部", "department");
    }
}