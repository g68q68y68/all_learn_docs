package com.bpm.example.demo.group;

import com.bpm.common.util.FlowableEngineUtil;
import com.bpm.example.demo.group.util.GroupUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.idm.api.Group;
import org.junit.Test;

@Slf4j
public class RunUpdateGroupDemo extends FlowableEngineUtil {
    @Test
    public void runUpdateGroupTest() {
        //加载Flowable配置文件并初始化流程引擎及服务
        loadFlowableConfigAndInitEngine("flowable.cfg.xml");
        GroupUtil groupUtil = new GroupUtil(identityService);
        //新建用户组
        groupUtil.addGroup("process_platform_department", "流程平台部", "department");
        //查询用户组信息
        Group oldGroup = groupUtil.executeSingleResult(identityService
                .createGroupQuery().groupId("process_platform_department"));
        //打印原始用户组信息
        log.info("修改前：id：{}，name：{}", oldGroup.getId(), oldGroup.getName());
        //修改用户组信息
        groupUtil.updateGroup("process_platform_department", "BPM平台部", "department");
        //再次查询用户组信息
        Group newGroup = groupUtil.executeSingleResult(identityService
                .createGroupQuery().groupId("process_platform_department"));
        log.info("修改后：id：{}，name：{}", newGroup.getId(), newGroup.getName());
    }
}