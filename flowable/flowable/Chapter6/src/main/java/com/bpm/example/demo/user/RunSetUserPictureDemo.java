package com.bpm.example.demo.user;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.user.util.UserUtil;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class RunSetUserPictureDemo extends FlowableEngineUtil {
    @Test
    public void runSetUserPictureDemo() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        UserUtil userUtil = new UserUtil(identityService);
        //新建用户
        userUtil.addUser("hebo", "贺", "波", "", "", "");
        //读取图片
        URL resource = RunSetUserPictureDemo.class.getClassLoader()
                .getResource("pictures/photo.png");
        File userPictureFile = new File(resource.getPath());
        //为用户设置图片
        userUtil.setPictureForUser("hebo", userPictureFile);
    }
}