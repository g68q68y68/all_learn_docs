package com.bpm.example.demo1.identity.group.data;

import com.alibaba.fastjson.JSON;
import com.bpm.example.demo1.identity.FlowableIdentityUtils;
import com.bpm.example.demo1.identity.group.entity.CustomGroupEntity;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.api.Group;
import org.flowable.idm.engine.impl.GroupQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.data.GroupDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomGroupDataManager implements GroupDataManager {

    /**
     * 创建一个用户组对象
     */
    @Override
    public GroupEntity create() {
        return new GroupEntityImpl();
    }

    /**
     * 调用第三方身份管理服务根据用户组主键查询用户组信息
     */
    @Override
    public GroupEntity findById(String entityId) {
        CustomGroupEntity groupEntity = new CustomGroupEntity();
        groupEntity.setGroupId(entityId);
        //为了节省篇幅，此处省略调用第三方身份管理服务根据用户组主键查询用户组信息代码，仅用打印日志替代
        log.info("调用第三方身份管理服务根据用户组主键{}查询用户组信息", entityId);
        return (GroupEntity) FlowableIdentityUtils.toFlowableGroup(groupEntity);
    }

    /**
     * 调用第三方身份管理服务接口新建用户组
     */
    @Override
    public void insert(GroupEntity entity) {
        CustomGroupEntity groupEntity = FlowableIdentityUtils.toCustomGroup(entity);
        //为了节省篇幅，此处省略调用第三方身份管理服务新建用户组代码，仅用打印日志替代
        log.info("调用第三方身份管理服务新建用户组：{}", JSON.toJSONString(groupEntity));
    }

    /**
     * 调用第三方身份管理服务修改用户组
     */
    @Override
    public GroupEntity update(GroupEntity entity) {
        CustomGroupEntity customGroupEntity = FlowableIdentityUtils.toCustomGroup(entity);
        //为了节省篇幅，此处省略调用第三方身份管理服务修改用户组代码，仅用打印日志替代
        log.info("调用第三方身份管理服务修改用户组：{}", JSON.toJSONString(customGroupEntity));
        return (GroupEntity) FlowableIdentityUtils.toFlowableGroup(customGroupEntity);
    }

    /**
     * 调用第三方身份管理服务删除用户组
     */
    @Override
    public void delete(String id) {
        //为节省篇幅，此处省略调用第三方身份管理服务删除用户组代码，仅用打印日志替代
        log.info("调用第三方身份管理服务删除主键为{}的用户组", id);
    }

    /**
     * 调用第三方身份管理服务删除用户
     */
    @Override
    public void delete(GroupEntity entity) {
        delete(entity.getId());
    }

    /**
     * 调用第三方身份管理服务查询符合条件的用户列表
     */
    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl groupQuery) {
        Map queryCriteriaMap = FlowableIdentityUtils.toQueryCriteriaMap(groupQuery);
        //为了节省篇幅，此处省略第三方身份管理服务根据查询条件查询用户组列表的逻辑，仅用打印日志替代
        log.info("调用第三方身份管理服务根据查询条件{}查询用户组列表",
                Joiner.on("&").withKeyValueSeparator("=").join(queryCriteriaMap));
        return new ArrayList<Group>();
    }

    /**
     * 调用第三方身份管理服务查询符合条件的用户数
     */
    @Override
    public long findGroupCountByQueryCriteria(GroupQueryImpl groupQuery) {
        Map queryCriteriaMap = FlowableIdentityUtils.toQueryCriteriaMap(groupQuery);
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户组数的逻辑，仅用打印日志替代
        log.info("调用第三方身份管理服务根据查询条件{}查询用户组数",
                Joiner.on("&").withKeyValueSeparator("=").join(queryCriteriaMap));
        return 0;
    }

    /**
     * 用第三方身份管理服务根据用户主键查询所在的用户组
     */
    @Override
    public List<Group> findGroupsByUser(String userId) {
        List<CustomGroupEntity> groupEntities = new ArrayList<>();
        //为节省篇幅，此处省略调用第三方身份管理服务根据用户主键查询所在组的代码，仅用打印日志替代
        List<Group> groups = new ArrayList<>();
        for (CustomGroupEntity customGroupEntity : groupEntities) {
            Group group = FlowableIdentityUtils.toFlowableGroup(customGroupEntity);
            groups.add(group);
        }
        log.info("调用第三方身份管理服务查询主键为{}的用户所在的用户组：{}", userId,
                JSON.toJSONString(groups));
        return groups;
    }

    @Override
    public List<Group> findGroupsByNativeQuery(Map<String, Object> map) {
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户列表的逻辑，读者根据需要自行实现
        return new ArrayList<Group>();
    }

    @Override
    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户数的逻辑，读者根据需要自行实现
        return 0;
    }

    @Override
    public List<Group> findGroupsByPrivilegeId(String s) {
        return new ArrayList<Group>();
    }
}