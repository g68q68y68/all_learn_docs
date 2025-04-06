package com.bpm.example.demo1.identity.group.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class CustomGroupEntity implements Serializable {
    //用户组编号
    private String groupId;
    //用户组名称
    private String groupName;
}