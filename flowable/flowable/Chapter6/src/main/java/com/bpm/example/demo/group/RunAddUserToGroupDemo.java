package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.junit.Test;

public class RunAddUserToGroupDemo extends FlowableEngineUtil {
    @Test
    public void runAddUserToGroupDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //新建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "hebo824@163.com", "******");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //新建用户组
        groupUtil.addGroup("process_platform_department", "流程平台部", "department");
        //将用户加入用户组
        identityService.createMembership("hebo", "process_platform_department");
    }
}