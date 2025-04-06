package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.api.User;
import org.junit.Test;

@Slf4j
public class RunUpdateUserDemo extends FlowableEngineUtil {
    @Test
    public void runUpdateUserDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //新建用户
        userUtil.addUser("hebo", "贺", "波", "诗雨花魂", "", "");
        //查询用户信息
        User oldUser = userUtil.executeSingleResult(identityService
                .createUserQuery().userId("hebo"));
        //打印原始用户信息
        log.info("修改前：id：{}，displayName：{}", oldUser.getId(), oldUser.getDisplayName());
        //修改用户显示名称
        userUtil.updateUser("hebo", oldUser.getFirstName(), oldUser.getLastName(), "爱者独语",
                "", "");
        //再次查询用户信息
        User newUser = userUtil.executeSingleResult(identityService
                .createUserQuery().userId("hebo"));
        log.info("修改后：id：{}，displayName：{}", newUser.getId(), newUser.getDisplayName());
    }
}