package com.bpm.example.demo1.identity;

import com.bpm.example.demo1.identity.group.entity.CustomGroupEntity;
import com.bpm.example.demo1.identity.membership.entity.CustomMembershipEntity;
import com.bpm.example.demo1.identity.user.entity.CustomUserEntity;
import org.flowable.common.engine.api.query.Query;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.MembershipEntity;
import org.flowable.idm.engine.impl.persistence.entity.MembershipEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实现自己定义的用户、用户组实体与Flowable中的user、group互转
 */
public class FlowableIdentityUtils {

    //将自定义用户实体转换为Flowable的User
    public static User toFlowableUser(CustomUserEntity user) {
        if (user != null) {
            User userEntity=new UserEntityImpl();
            userEntity.setId(user.getUserName());
            if (user.getRealName() != null) {
                userEntity.setFirstName(user.getRealName().substring(0,1));
                userEntity.setLastName(user.getRealName().substring(1));
            }
            userEntity.setEmail(user.getEmail());
            userEntity.setPassword(user.getPassword());
            return userEntity;
        }
        return null;
    }

    //将Flowable的User转换为将自定义用户实体
    public static CustomUserEntity toCustomUser(User user) {
        if (user != null) {
            CustomUserEntity customUserEntity = new CustomUserEntity();
            customUserEntity.setUserName(user.getId());
            customUserEntity.setRealName(user.getFirstName() + user.getLastName());
            customUserEntity.setEmail(user.getEmail());
            customUserEntity.setPassword(user.getPassword());
            return customUserEntity;
        }
        return null;
    }

    //将自定义用户组实体转换为Flowable的Group
    public static Group toFlowableGroup(CustomGroupEntity group) {
        if (group != null) {
            Group groupEntity=new GroupEntityImpl();
            groupEntity.setId(group.getGroupId());
            groupEntity.setType("CustomGroup");
            groupEntity.setName(group.getGroupName());
            return groupEntity;
        }
        return null;
    }

    //将Flowable的Group转换为自定义用户组实体
    public static CustomGroupEntity toCustomGroup(Group group) {
        if (group != null) {
            CustomGroupEntity customGroupEntity = new CustomGroupEntity();
            customGroupEntity.setGroupId(group.getId());
            customGroupEntity.setGroupName(group.getName());
            return customGroupEntity;
        }
        return null;
    }

    //将自定义关系实体转换为Flowable的MembershipEntity
    public static MembershipEntity toFlowableMembershipEntity(CustomMembershipEntity customMembershipEntity) {
        if (customMembershipEntity != null) {
            MembershipEntity membershipEntity=new MembershipEntityImpl();
            membershipEntity.setUserId(customMembershipEntity.getUserName());
            membershipEntity.setGroupId(customMembershipEntity.getGroupId());
            return membershipEntity;
        }
        return null;
    }

    //将Flowable的MembershipEntity转换为自定义关系实体
    public static CustomMembershipEntity toCustomMembershipEntity(MembershipEntity membershipEntity) {
        if (membershipEntity != null) {
            CustomMembershipEntity customMembershipEntity = new CustomMembershipEntity();
            customMembershipEntity.setGroupId(membershipEntity.getGroupId());
            customMembershipEntity.setUserName(membershipEntity.getUserId());
            return customMembershipEntity;
        }
        return null;
    }

    //将Flowable的Query对象转为Map
    public static Map<String, Object> toQueryCriteriaMap(Query query) {
        Map<String, Object> queryCriteriaMap = new HashMap<>();
        try{
            //通过getDeclaredFields()方法获取对象类中的所有属性（含私有）
            Field[] fields = query.getClass().getDeclaredFields();
            for (Field field : fields) {
                //设置允许通过反射访问私有变量
                field.setAccessible(true);
                //过滤掉private类型的属性
                if(Modifier.isPrivate(field.getModifiers())) {
                    continue;
                }
                //获取字段的值
                Object value = field.get(query);
                if (Objects.nonNull(value)) {
                    //获取字段属性名称
                    String name = field.getName();
                    queryCriteriaMap.put(name, value);
                }
            }
        }
        catch (Exception ex){
            //处理异常
        }
        return queryCriteriaMap;
    }
}