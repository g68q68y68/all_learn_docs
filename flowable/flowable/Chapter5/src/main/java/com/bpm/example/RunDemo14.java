package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;

import java.util.List;

public class RunDemo14 extends FlowableEngineUtil {
    public static void main(String[] args) {
        RunDemo14 demo = new RunDemo14();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //创建用户1
        User user1 = identityService.newUser("huhaiqin");
        user1.setFirstName("胡");
        user1.setLastName("海琴");
        user1.setEmail("huhaiqin@flowable.org");
        //保存用户1
        identityService.saveUser(user1);
        //设置用户扩展信息
        identityService.setUserInfo(user1.getId(), "age", "28");
        //创建用户2
        User user2 = identityService.newUser("liuxiaopeng");
        user2.setFirstName("刘");
        user2.setLastName("晓鹏");
        user2.setEmail("liuxiaopeng@flowable.org");
        //保存用户2
        identityService.saveUser(user2);
        //创建用户3
        User user3 = identityService.newUser("hebo");
        user3.setFirstName("贺");
        user3.setLastName("波");
        user3.setEmail("hebo@flowable.org");
        //保存用户3
        identityService.saveUser(user3);

        //创建组1
        Group group1 = identityService.newGroup("group1");
        group1.setName("Group1");
        //保存组1
        identityService.saveGroup(group1);
        //创建组2
        Group group2 = identityService.newGroup("group2");
        group2.setName("Group2");
        //保存组2
        identityService.saveGroup(group2);

        //创建用户与组的关系
        identityService.createMembership("huhaiqin", "group1");
        identityService.createMembership("liuxiaopeng", "group1");
        identityService.createMembership("hebo", "group2");

        //查询组和用户
        List<Group> groups = identityService.createGroupQuery().list();
        for (Group group : groups) {
            System.out.println("组名：" + group.getName() + "，组ID：" + group.getId());
            String groupId = group.getId();
            List<User> users = identityService.createUserQuery().memberOfGroup(groupId).list();
            for (User user : users) {
                System.out.println("--成员ID：" + user.getId()
                        + "，姓名：" + user.getFirstName() + user.getLastName()
                        + "，邮箱：" + user.getEmail());
            }
        }
        String age = identityService.getUserInfo(user1.getId(), "age");
        System.out.println("--用户扩展信息，ID：" + user1.getId() + "，age: " + age);
    }
}

