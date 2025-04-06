package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

@Slf4j
public class RunSingleResultUsersDemo extends FlowableEngineUtil {
    @Test
    public void runSingleResultUsersDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //创建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "", "");
        userUtil.addUser("liuxiaopeng", "刘", "晓鹏", "阿凡提", "", "");
        userUtil.addUser("huhaiqin", "胡", "海琴", "清波~微醉", "", "");
        //获查询符合条件的单个用户
        UserQuery userQuery = identityService.createUserQuery()
                .userLastName("贺").userFirstName("波");
        User user = userUtil.executeSingleResult(userQuery);
        if (user != null) {
            log.info("用户编号：{}，姓名：{}，显示名：{}", user.getId(),
                    user.getLastName() + user.getFirstName(), user.getDisplayName());
        }
    }
}
