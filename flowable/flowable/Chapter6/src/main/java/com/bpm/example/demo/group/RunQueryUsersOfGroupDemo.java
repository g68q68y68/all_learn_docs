package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

public class RunQueryUsersOfGroupDemo extends FlowableEngineUtil {
    @Test
    public void runQueryUsersOfGroupDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //新建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "hebo824@163.com", "");
        userUtil.addUser("liuxiaopeng", "刘", "晓鹏", "阿凡提", "lxpcnic@163.com", "");
        userUtil.addUser("huhaiqin", "胡", "海琴", "清波~微醉", "aiqinhai_hu@163.com", "");
        userUtil.addUser("wangjunlin", "王", "俊林", "木秀于林", "wangjl@163.com", "");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //新建用户组
        groupUtil.addGroup("process_platform_department", "流程平台部", "department");
        //将用户加入用户组
        groupUtil.addUserToGroup("hebo", "process_platform_department");
        groupUtil.addUserToGroup("liuxiaopeng", "process_platform_department");
        groupUtil.addUserToGroup("huhaiqin", "process_platform_department");
        groupUtil.addUserToGroup("wangjunlin", "process_platform_department");
        //查询用户组中的用户
        UserQuery userQuery = identityService.createUserQuery()
                .memberOfGroup("process_platform_department")
                .orderByUserId().asc();
        userUtil.executeList(userQuery);
    }
}