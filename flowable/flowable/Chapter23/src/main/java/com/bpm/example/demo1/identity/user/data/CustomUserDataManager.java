package com.bpm.example.demo1.identity.user.data;

import com.alibaba.fastjson.JSON;
import com.bpm.example.demo1.identity.FlowableIdentityUtils;
import com.bpm.example.demo1.identity.user.entity.CustomUserEntity;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;

import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.data.UserDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomUserDataManager implements UserDataManager {

    /**
     * 创建一个用户对象
     */
    @Override
    public UserEntity create() {
        return new UserEntityImpl();
    }

    /**
     * 调用第三方身份管理服务根据用户主键查询用户信息
     */
    @Override
    public UserEntity findById(String entityId) {
        CustomUserEntity userEntity = new CustomUserEntity();
        userEntity.setUserName(entityId);
        //为了节省篇幅，此处省略调用第三方身份管理服务根据用户主键查询用户信息的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务根据用户主键{}查询用户信息", entityId);
        return (UserEntity) FlowableIdentityUtils.toFlowableUser(userEntity);
    }

    /**
     * 调用第三方身份管理服务新建用户
     */
    @Override
    public void insert(UserEntity entity) {
        CustomUserEntity userEntity = FlowableIdentityUtils.toCustomUser(entity);
        //为了节省篇幅，此处省略调用第三方身份管理服务新建用户的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务新建用户：{}", JSON.toJSONString(userEntity));
    }

    /**
     * 调用第三方身份管理服务修改用户
     */
    @Override
    public UserEntity update(UserEntity entity) {
        CustomUserEntity customUserEntity = FlowableIdentityUtils.toCustomUser(entity);
        //为了节省篇幅，此处省略调用第三方身份管理服务修改用户的代码，仅用打印日志替代
        log.info("调用第三方身份管理服务修改用户：{}", JSON.toJSONString(customUserEntity));
        return (UserEntity) FlowableIdentityUtils.toFlowableUser(customUserEntity);
    }

    /**
     * 调用第三方身份管理服务删除用户
     */
    @Override
    public void delete(String id) {
        //为了节省篇幅，此处省略调用外第三方身份管理服务删除用户代码，仅用打印日志替代
        log.info("调用第三方身份管理服务删除主键为{}的用户", id);
    }

    /**
     * 调用第三方身份管理服务删除用户
     */
    @Override
    public void delete(UserEntity entity) {
        delete(entity.getId());
    }

    /**
     * 调用第三方身份管理服务查询符合条件的用户列表
     */
    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl userQuery) {
        Map queryCriteriaMap = FlowableIdentityUtils.toQueryCriteriaMap(userQuery);
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户列表的逻辑，仅用打印日志替代
        log.info("调用第三方身份管理服务根据查询条件{}查询用户列表",
                Joiner.on("&").withKeyValueSeparator("=").join(queryCriteriaMap));
        return new ArrayList<User>();
    }

    /**
     * 调用第三方身份管理服务查询符合条件的用户数
     * @param userQuery
     * @return
     */
    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl userQuery) {
        Map queryCriteriaMap = FlowableIdentityUtils.toQueryCriteriaMap(userQuery);
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户数的逻辑，仅用打印日志替代
        log.info("调用第三方身份管理服务根据查询条件{}查询用户数",
                Joiner.on("&").withKeyValueSeparator("=").join(queryCriteriaMap));
        return 0;
    }

    @Override
    public List<User> findUsersByNativeQuery(Map<String, Object> map) {
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户列表的逻辑，读者根据需要自行实现
        return new ArrayList<User>();
    }

    @Override
    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        //为了节省篇幅，此处省略调用第三方身份管理服务根据查询条件查询用户数的逻辑，读者根据需要自行实现
        return 0;
    }

    @Override
    public List<User> findUsersByPrivilegeId(String s) {
        return null;
    }
}

