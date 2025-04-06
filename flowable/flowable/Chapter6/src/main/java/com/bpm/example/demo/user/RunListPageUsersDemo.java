package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

public class RunListPageUsersDemo extends FlowableEngineUtil {
    @Test
    public void runListPageUsersDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //创建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "hebo824@163.com", "");
        userUtil.addUser("tonyhebo", "贺", "博", "唐尼", "tonyhebo@163.com", "");
        userUtil.addUser("zhanghe", "张", "禾", "禾苗", "zhanghe@qq.com", "");
        userUtil.addUser("liheng", "李", "横", "横刀立马", "liheng@qq.com", "");
        userUtil.addUser("liuxiaopeng", "刘", "晓鹏", "阿凡提", "lxpcnic@163.com", "");
        userUtil.addUser("huhaiqin", "胡", "海琴", "轻波~微醉", "aiqinhai_hu@163.com", "");
        //分页查询用户并打印
        UserQuery userQuery = identityService.createUserQuery()
                .userEmailLike("%he%").orderByUserId().desc();
        userUtil.executeListPage(userQuery, 1, 3);
    }
}