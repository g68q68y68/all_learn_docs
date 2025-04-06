package com.bpm.example.demo.group.util;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;

import java.util.List;

@Slf4j
public class GroupUtil {

    private IdentityService identityService;

    public GroupUtil(IdentityService identityService) {
        this.identityService = identityService;
    }

    /**
     * 新建用户组
     * @param id                用户组编号
     * @param name              用户组名称
     * @param type              用户组类型
     */
    public void addGroup(String id, String name, String type) {
        //调用IdentityService的newGroup()方法创建Group实例
        Group newGroup = identityService.newGroup(id);
        //调用setter方法为创建的Group实例设置属性值
        newGroup.setName(name);
        newGroup.setType(type);
        //调用IdentityService的saveGroup方法将Group实例保存到数据库中
        identityService.saveGroup(newGroup);
    }

    /**
     * 执行GroupQuery的list方法
     * @param groupQuery
     */
    public void executeList(GroupQuery groupQuery) {
        List<Group> list = groupQuery.list();
        for (Group group : list) {
            log.info("用户组编号：{}，名称：{}，类型：{}", group.getId(), group.getName(), group.getType());
        }
    }

    /**
     * 执行GroupQuery的list方法
     * @param groupQuery
     */
    public void executeListPage(GroupQuery groupQuery, int firstResult, int maxResults) {
        List<Group> list = groupQuery.listPage(firstResult, maxResults);
        for (Group group : list) {
            log.info("用户组编号：{}，名称：{}，类型：{}", group.getId(), group.getName(), group.getType());
        }
    }

    /**
     * 执行GroupQuery的count方法
     * @param groupQuery
     * @return
     */
    public void executeCount(GroupQuery groupQuery) {
        long groupNum = groupQuery.count();
        log.info("用户组数为：{}", groupNum);
    }

    /**
     * 执行GroupQuery的singleResult方法
     * @param groupQuery
     */
    public Group executeSingleResult(GroupQuery groupQuery) {
        Group group = groupQuery.singleResult();
        return group;
    }

    /**
     * 修改用户组信息
     * @param groupId            用户组编号
     * @param newName           新用户组名
     * @param newType           新用户组类型
     */
    public void updateGroup(String groupId, String newName, String newType) {
        //查询用户组信息
        Group group = executeSingleResult(identityService.createGroupQuery().groupId(groupId));
        //调用setter方法为创建的Group实例设置属性值
        group.setName(newName);
        group.setType(newType);
        //调用IdentityService的saveGroup方法将Group实例保存到数据库中
        identityService.saveGroup(group);
    }

    /**
     * 删除用户组
     * @param groupId             用户组编号
     */
    public void deleteGroup(String groupId) {
        identityService.deleteGroup(groupId);
    }

    /**
     * 将用户加入到用户组中
     * @param userId            用户编号
     * @param groupId           用户组编号
     */
    public void addUserToGroup(String userId, String groupId) {
        identityService.createMembership(userId, groupId);
    }

    /**
     * 将用户从用户组中移除
     * @param userId            用户编号
     * @param groupId           用户组编号
     */
    public void removeUserFromGroup(String userId, String groupId) {
        identityService.deleteMembership(userId, groupId);
    }
}
