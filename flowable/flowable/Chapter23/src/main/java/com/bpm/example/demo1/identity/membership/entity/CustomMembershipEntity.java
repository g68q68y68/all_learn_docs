package com.bpm.example.demo1.identity.membership.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class CustomMembershipEntity implements Serializable {
    //关联关系编号
    private String id;
    //用户名
    private String userName;
    //用户组编号
    private String groupId;
}