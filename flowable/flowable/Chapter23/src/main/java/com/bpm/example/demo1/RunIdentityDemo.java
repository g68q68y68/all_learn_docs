package com.bpm.example.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.junit.Test;

import java.util.List;

@Slf4j
public class RunIdentityDemo extends FlowableEngineUtil {
    @Test
    public void runIdentityDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.custom-identity.xml");

        //新建用户实例
        User newUser = identityService.newUser("zhangsan");
        newUser.setFirstName("张");
        newUser.setLastName("三");
        newUser.setEmail("zhangsan@qq.com");
        newUser.setPassword("******");
        //保存用户信息
        identityService.saveUser(newUser);

        //查询用户列表
        List<User> userList = identityService.createUserQuery()
                .userId("zhangsan").userEmailLike("zhangsan").list();

        //修改用户信息
        newUser.setLastName("三丰");
        newUser.setEmail("zhangsan@163.com");
        newUser.setPassword("######");
        ((UserEntity) newUser).setRevision(1);
        identityService.saveUser(newUser);

        //创建用户组
        Group newGroup = identityService.newGroup("group1");
        newGroup.setName("高管用户组");
        identityService.saveGroup(newGroup);

        //将用户加入用户组
        identityService.createMembership(newUser.getId(), newGroup.getId());

        //删除用户信息
        identityService.deleteUser(newUser.getId());
        //删除用户组信息
        identityService.deleteGroup(newGroup.getId());
        //删除关联关系
        identityService.deleteMembership(newUser.getId(), newGroup.getId());
    }
}