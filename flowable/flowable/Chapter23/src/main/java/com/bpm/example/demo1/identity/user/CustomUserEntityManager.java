package com.bpm.example.demo1.identity.user;

import org.flowable.idm.api.PasswordEncoder;
import org.flowable.idm.api.PasswordSalt;
import org.flowable.idm.api.Picture;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.AbstractIdmEngineEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.data.UserDataManager;

import java.util.List;
import java.util.Map;

public class CustomUserEntityManager extends AbstractIdmEngineEntityManager<UserEntity,
        UserDataManager> implements UserEntityManager {

    public CustomUserEntityManager(IdmEngineConfiguration idmEngineConfiguration,
                                   UserDataManager dataManager) {
        super(idmEngineConfiguration, dataManager);
    }

    /**
     * 创建一个用户对象
     */
    @Override
    public User createNewUser(String userId) {
        UserEntity userEntity = create();
        userEntity.setId(userId);
        userEntity.setRevision(0);
        return userEntity;
    }

    /**
     * 修改用户信息
     */
    @Override
    public void updateUser(User updatedUser) {
        super.update((UserEntity) updatedUser);
    }

    /**
     * 调用用户组数据管理器查询符合条件的用户列表
     */
    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl userQuery) {
        return dataManager.findUserByQueryCriteria(userQuery);
    }

    /**
     * 调用用户组数据管理器查询符合条件的用户列表数
     */
    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl query) {
        return dataManager.findUserCountByQueryCriteria(query);
    }

    @Override
    public UserQuery createNewUserQuery() {
        return new UserQueryImpl(this.getCommandExecutor());
    }

    @Override
    public Boolean checkPassword(String s, String s1, PasswordEncoder passwordEncoder,
                                 PasswordSalt passwordSalt) {
        return null;
    }

    @Override
    public List<User> findUsersByNativeQuery(Map<String, Object> map) {
        return dataManager.findUsersByNativeQuery(map);
    }

    @Override
    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        return dataManager.findUserCountByNativeQuery(parameterMap);
    }

    @Override
    public boolean isNewUser(User user) {
        return ((UserEntity) user).getRevision() == 0;
    }

    @Override
    public Picture getUserPicture(User user) {
        return null;
    }

    @Override
    public void setUserPicture(User user, Picture picture) {

    }

    @Override
    public void deletePicture(User user) {

    }

    @Override
    public List<User> findUsersByPrivilegeId(String s) {
        return null;
    }
}