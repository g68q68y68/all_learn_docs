package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

public class RunCountUsersDemo extends FlowableEngineUtil {
    @Test
    public void runCountUsersDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //创建用户
        userUtil.addUser("hebo", "贺", "波", "", "hebo824@163.com", "");
        userUtil.addUser("tonyhebo", "贺", "博", "", "tonyhebo@163.com", "");
        userUtil.addUser("zhanghe", "张", "禾", "", "zhanghe@qq.com", "");
        userUtil.addUser("liheng", "李", "横", "", "liheng@qq.com", "");
        userUtil.addUser("liuxiaopeng", "刘", "晓鹏", "", "lxpcnic@163.com", "");
        userUtil.addUser("huhaiqin", "胡", "海琴", "", "aiqinhai_hu@163.com", "");
        //查询符合条件的用户数
        UserQuery userQuery = identityService.createUserQuery()
                .userLastName("贺").userEmailLike("%he%");
        userUtil.executeCount(userQuery);
    }
}
