package com.bpm.example.demo1.identity.group;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.GroupQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.AbstractIdmEngineEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.data.GroupDataManager;

import java.util.List;
import java.util.Map;

public class CustomGroupEntityManager extends AbstractIdmEngineEntityManager<GroupEntity, GroupDataManager> implements GroupEntityManager {

    public CustomGroupEntityManager(IdmEngineConfiguration idmEngineConfiguration, GroupDataManager dataManager) {
        super(idmEngineConfiguration, dataManager);
    }

    /**
     * 创建一个用户组对象
     * @param groupId
     * @return
     */
    public Group createNewGroup(String groupId) {
        GroupEntity groupEntity = dataManager.create();
        groupEntity.setId(groupId);
        groupEntity.setRevision(0);
        return groupEntity;
    }

    /**
     * 根据用户编号查询所在的用户组
     * @param userId
     * @return
     */
    public List<Group> findGroupsByUser(String userId) {
        return dataManager.findGroupsByUser(userId);
    }

    public List<Group> findGroupsByNativeQuery(Map<String, Object> map) {
        return null;
    }

    public boolean isNewGroup(Group group) {
        return ((GroupEntity) group).getRevision() == 0;
    }

    public List<Group> findGroupsByPrivilegeId(String s) {
        return null;
    }

    public GroupQuery createNewGroupQuery() {
        return new GroupQueryImpl(getCommandExecutor());
    }

    public List<Group> findGroupByQueryCriteria(GroupQueryImpl groupQuery) {
        return null;
    }

    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
        return dataManager.findGroupCountByQueryCriteria(query);
    }

    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        return dataManager.findGroupCountByNativeQuery(parameterMap);
    }
}