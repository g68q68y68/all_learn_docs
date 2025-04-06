package com.bpm.example.demo1;

import com.bpm.common.util.FlowableEngineUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
public class RunMqTaskDemo extends FlowableEngineUtil {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void runMqTaskDemo() throws Exception {
        //加载Flowable配置文件并初始化流程引擎及服务
        initFlowableEngineAndServices("flowable.mqtask.xml");
        //部署流程
        deployResource("processes/RestMqTaskProcess.bpmn20.xml");

        //设置流程变量
        Map<String, Object> varMap = ImmutableMap.of("userName", "hebo",
                "realName", "贺波", "registerAddress", "北京",
                "registerTime", sdf.format(new Date()));
        //启动流程
        ProcessInstance procInst = runtimeService
                .startProcessInstanceByKey("mqTaskProcess", varMap);
        //查询第一个任务
        Task task = taskService.createTaskQuery().processInstanceId(procInst.getId())
                .singleResult();
        //办理第一个任务
        taskService.complete(task.getId());

        //从ActiveMQ服务查询消息
        fetchActiveMQMessage();
    }

    private void fetchActiveMQMessage() throws Exception {
        //创建连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory
                = new ActiveMQConnectionFactory("tcp://localhost:61616");
        //获得连接
        Connection connection = activeMQConnectionFactory.createConnection();
        //启动访问
        connection.start();
        while (true) {
            //创建会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建队列
            Destination destination = session.createQueue("userRegisterQueue");
            //创建消费者
            MessageConsumer consumer = session.createConsumer(destination);
            //读取消息
            TextMessage message = (TextMessage) consumer.receive();
            String text = message.getText();
            log.info("用户注册成功，注册信息为：{}", text);
            consumer.close();
            session.close();
        }
    }
}