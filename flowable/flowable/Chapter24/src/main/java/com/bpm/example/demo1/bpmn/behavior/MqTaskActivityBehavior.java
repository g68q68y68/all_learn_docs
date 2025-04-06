package com.bpm.example.demo1.bpmn.behavior;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MqTaskActivityBehavior extends AbstractBpmnActivityBehavior {

    private static final long serialVersionUID = 1L;
    //ActiveMQ服务器地址
    protected Expression brokerURL;
    //队列名称
    protected Expression activeQueue;
    //消息内容
    protected Expression messageText;
    //是否忽略异常
    protected Expression ignoreException;

    @Override
    public void execute(DelegateExecution execution) {
        //获取各属性的值
        String brokerURLStr = getStringFromField(brokerURL, execution);
        String activeQueueStr = getStringFromField(activeQueue, execution);
        String messageTextStr = getStringFromField(messageText, execution);
        String ignoreExceptionStr = getStringFromField(ignoreException, execution);
        //执行发送mq消息
        executeJmsProduce(execution, brokerURLStr, activeQueueStr, messageTextStr,
                ignoreExceptionStr);
        //离开当前节点
        leave(execution);
    }

    private void executeJmsProduce(DelegateExecution execution, String brokerURLStr,
                                   String activeQueueStr, String messageTextStr,
                                   String ignoreExceptionStr) {
        try {
            //创建连接工厂
            ActiveMQConnectionFactory activeMQConnectionFactory
                    = new ActiveMQConnectionFactory(brokerURLStr);
            //获得连接
            Connection connection = activeMQConnectionFactory.createConnection();
            //启动访问
            connection.start();
            //创建会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建队列
            Queue queue = session.createQueue(activeQueueStr);
            //创建消息的生产者
            MessageProducer producer = session.createProducer(queue);
            //创建消息
            TextMessage textMessage = session.createTextMessage(messageTextStr);
            //发送消息
            producer.send(textMessage);
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            //抛出流程异常
            if (!"true".equals(ignoreExceptionStr)) {
                BpmnError error = new BpmnError("mqTaskError", e.getMessage());
                ErrorPropagation.propagateError(error, execution);
            }
        }
    }

    //查询表达式的值
    private String getStringFromField(Expression expression, DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}