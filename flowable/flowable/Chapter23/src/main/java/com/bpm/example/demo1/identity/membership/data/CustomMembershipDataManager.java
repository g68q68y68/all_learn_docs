package com.bpm.example.demo1.identity.membership.data;

import com.alibaba.fastjson.JSON;
import com.bpm.example.demo1.identity.FlowableIdentityUtils;
import com.bpm.example.demo1.identity.membership.entity.CustomMembershipEntity;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.engine.impl.persistence.entity.MembershipEntity;
import org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.data.MembershipDataManager;

@Slf4j
public class CustomMembershipDataManager implements MembershipDataManager {

    /**
     * 创建一个关联关系
     */
    public MembershipEntity create() {
        return new MembershipEntityImpl();
    }

    /**
     * 调用第三方身份管理服务根据关联关系主键查询关联关系对象
     */
    public MembershipEntity findById(String entityId) {
        CustomMembershipEntity customMembershipEntity = new CustomMembershipEntity();
        customMembershipEntity.setId(entityId);
        //为了节省篇幅，此处省略调用第三方身份管理服务根据关联关系主键查询关联关系对象的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务根据关联关系主键{}查询关联关系信息", entityId);
        return (MembershipEntity) FlowableIdentityUtils.toFlowableMembershipEntity(customMembershipEntity);
    }

    /**
     * 调用第三方身份管理服务新建关联关系
     */
    public void insert(MembershipEntity entity) {
        CustomMembershipEntity customMembershipEntity = FlowableIdentityUtils.toCustomMembershipEntity(entity);
        //为了节省篇幅，此处省略调用第三方身份管理服务新建关联关系的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务新建关联关系：{}", JSON.toJSONString(customMembershipEntity));
    }

    /**
     * 调用第三方身份管理服务修改关联关系
     */
    public MembershipEntity update(MembershipEntity entity) {
        CustomMembershipEntity customMembershipEntity = FlowableIdentityUtils.toCustomMembershipEntity(entity);
        //为了节省篇幅，此处省略调用第三方身份管理服务修改关联关系的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务修改关联关系：{}", JSON.toJSONString(customMembershipEntity));
        return (MembershipEntity) FlowableIdentityUtils.toFlowableMembershipEntity(customMembershipEntity);
    }

    /**
     * 调用第三方身份管理服务删除关联关系
     */
    public void deleteMembership(String userId, String groupId) {
        //为节省篇幅，此处省略调用第三方身份管理服务删除管理关系的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务删除userId为{}、groupId为{}的关联关系", userId, groupId);
    }

    /**
     * 删除用户组的所有组员
     */
    public void deleteMembershipByGroupId(String groupId) {
        //为节省篇幅，此处省略调用第三方身份管理服务删除用户组所有组员的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务删除groupId为{}的用户组的所有组员", groupId);
    }

    /**
     * 将用户从所有用户组中移除
     */
    public void deleteMembershipByUserId(String userId) {
        //为节省篇幅，此处省略调用第三方身份管理服务将用户从所有用户组中移除的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务将用户{}从所有用户组中移除", userId);
    }

    /**
     * 调用第三方身份管理服务删除关联关系
     */
    public void delete(String id) {
       //为节省篇幅，此处省略调用第三方身份管理服务删除关联关系代码，仅用打印日志替代
        log.info("调用第三方身份管理服务删除主键为{}的关联关系", id);
    }

    /**
     * 调用第三方身份管理服务删除关联关系
     */
    public void delete(MembershipEntity entity) {
        delete(entity.getId());
    }
}
