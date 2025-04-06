package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.junit.Test;

@Slf4j
public class RunDeleteUserDemo extends FlowableEngineUtil {
    @Test
    public void runDeleteUserDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //新建用户
        userUtil.addUser("zhangsan", "张", "三", "三丰", "zhangsan@qq.com", "******");
        //查询用户信息
        User user = userUtil.executeSingleResult(identityService
                .createUserQuery().userId("zhangsan"));
        log.info("用户编号：{}，姓名：{}，邮箱：{}", user.getId(), user.getLastName() + user.getFirstName(), user.getEmail());
        //删除用户
        userUtil.deleteUser("zhangsan");
        //再次查询用户信息
        user = userUtil.executeSingleResult(identityService
                .createUserQuery().userId("zhangsan"));
        if (user == null) {
            log.error("用户编号为{}的用户不存在", "zhangsan");
        }
    }
}