package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

public class RunListUsersByConditionDemo extends FlowableEngineUtil {
    @Test
    public void runListUsersByConditionDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //创建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "hebo824@163.com", "");
        userUtil.addUser("tonyhebo", "贺", "博", "唐尼", "tonyhebo@163.com", "");
        userUtil.addUser("liuxiaopeng", "刘", "晓鹏", "阿凡提", "lxpcnic@163.com", "");
        userUtil.addUser("huhaiqin", "胡", "海琴", "清波~微醉", "aiqinhai_hu@163.com", "");
        //根据查询条件查询匹配用户并输出用户信息
        UserQuery userQuery = identityService.createUserQuery()
                .userLastName("贺").userEmailLike("%163.com%")
                .orderByUserId().asc();
        userUtil.executeList(userQuery);
    }
}