package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

public class RunListAllUsersDemo extends FlowableEngineUtil {
    @Test
    public void runListAllUsersDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //创建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "hebo824@163.com", "");
        userUtil.addUser("liuxiaopeng", "刘", "晓鹏", "阿凡提", "lxpcnic@163.com", "");
        userUtil.addUser("huhaiqin", "胡", "海琴", "清波~微醉", "aiqinhai_hu@163.com", "");
        //初始化UserQuery
        UserQuery userQuery = identityService.createUserQuery();
        //查询所有用户
        userUtil.executeList(userQuery);
    }
}