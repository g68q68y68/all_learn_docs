package com.bpm.example.demo1.identity.user.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class CustomUserEntity implements Serializable {
    //用户名
    private String userName;
    //真实姓名
    private String realName;
    //电子邮箱
    private String email;
    //密码
    private String password;
}
