package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.junit.Test;

public class RunAddUserDemo extends FlowableEngineUtil {
    @Test
    public void runAddUserDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "hebo824@163.com", "******");
    }
}