package com.bpm.example.demo2.mule.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class IpInfo implements Serializable {
    //IP
    private String ip;
    //省份
    private String pro;
    //城市
    private String city;
    //互联网服务提供商
    private String addr;
}
