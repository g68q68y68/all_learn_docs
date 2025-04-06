package com.bpm.example.demo1.identity.membership;

import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.persistence.entity.AbstractIdmEngineEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.MembershipEntity;
import org.flowable.idm.engine.impl.persistence.entity.MembershipEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.data.MembershipDataManager;

public class CustomMembershipEntityManager extends AbstractIdmEngineEntityManager<MembershipEntity, MembershipDataManager> implements MembershipEntityManager {

    public CustomMembershipEntityManager(IdmEngineConfiguration idmEngineConfiguration, MembershipDataManager dataManager) {
        super(idmEngineConfiguration, dataManager);
    }

    /**
     * 创建用户和用户组的关联关系
     * @param userId
     * @param groupId
     */
    public void createMembership(String userId, String groupId) {
        MembershipEntity membershipEntity = dataManager.create();
        membershipEntity.setUserId(userId);
        membershipEntity.setGroupId(groupId);
        dataManager.insert(membershipEntity);
    }

    /**
     * 删除用户和用户组的关联关系
     * @param userId
     * @param groupId
     */
    public void deleteMembership(String userId, String groupId) {
        dataManager.deleteMembership(userId, groupId);
    }

    /**
     * 删除用户组的所有组员
     * @param groupId
     */
    public void deleteMembershipByGroupId(String groupId) {
        dataManager.deleteMembershipByGroupId(groupId);
    }

    /**
     * 删除用户与所有用户组的关联关系
     * @param userId
     */
    public void deleteMembershipByUserId(String userId) {
        dataManager.deleteMembershipByUserId(userId);
    }
}
