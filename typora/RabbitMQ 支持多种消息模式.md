# RabbitMQ 支持多种消息模式

## 主要有以下几种类型：

1. 简单模式（Simple）
2. 工作队列模式（Work Queue）
3. 发布/订阅模式（Publish/Subscribe）
4. 路由模式（Routing）
5. 主题模式（Topics）
6. RPC模式（Remote Procedure Call）


让我们逐一讲解这些模式，并用图表来展示它们的工作方式。

1. 简单模式（Simple）


这是最基本的消息模式，一个生产者发送消息到一个队列，一个消费者接收消息。

![image-20241124192936808](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241124192936808.png)

2. 工作队列模式（Work Queue）


多个消费者共享一个队列中的任务，实现任务的负载均衡。（轮询）

![image-20241124193011249](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241124193011249.png)

3. 发布/订阅模式（Publish/Subscribe）


消息被广播到所有的消费者。（扇形）

![image-20241124193030261](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241124193030261.png)

4. 路由模式（Routing）


消息根据路由键（Routing Key）被选择性地路由到不同的队列。

![image-20241124193043258](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241124193043258.png)

5. 主题模式（Topics）


消息根据主题（Topic）被路由到一个或多个队列。主题可以包含通配符。

![image-20241124193053975](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241124193053975.png)

6. RPC模式（Remote Procedure Call）


客户端发送请求消息，服务器回复响应消息。

![image-20241124193112367](/Users/ganqingyao/Library/Application Support/typora-user-images/image-20241124193112367.png)



这些模式的主要区别在于：

1. 消息的分发方式（一对一、一对多、选择性分发等）
2. 交换机的类型和使用（是否使用交换机，使用哪种类型的交换机）
3. 路由策略（是否使用路由键，如何匹配路由键）
4. 消费者的数量和消息的处理方式


选择合适的模式取决于你的具体应用场景，如消息的分发需求、处理的复杂度、系统的扩展性要求等。RabbitMQ的这些模式提供了灵活的消息传递方案，能够满足各种不同的业务需求。







## 代码使用：



首先,确保您的Spring Boot项目中已经添加了RabbitMQ的依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

现在,让我们创建`RabbitConfig`类和相应的监听器:

```java
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 1. 简单模式
    @Bean
    public Queue simpleQueue() {
        return new Queue("simple.queue");
    }

    // 2. 工作队列模式
    @Bean
    public Queue workQueue() {
        return new Queue("work.queue");
    }

    // 3. 发布/订阅模式
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout.exchange");
    }

    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout.queue1");
    }

    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout.queue2");
    }

    @Bean
    public Binding fanoutBinding1(FanoutExchange fanoutExchange, Queue fanoutQueue1) {
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    @Bean
    public Binding fanoutBinding2(FanoutExchange fanoutExchange, Queue fanoutQueue2) {
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }

    // 4. 路由模式
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct.exchange");
    }

    @Bean
    public Queue directQueue1() {
        return new Queue("direct.queue1");
    }

    @Bean
    public Queue directQueue2() {
        return new Queue("direct.queue2");
    }

    @Bean
    public Binding directBinding1(DirectExchange directExchange, Queue directQueue1) {
        return BindingBuilder.bind(directQueue1).to(directExchange).with("error");
    }

    @Bean
    public Binding directBinding2(DirectExchange directExchange, Queue directQueue2) {
        return BindingBuilder.bind(directQueue2).to(directExchange).with("info");
    }

    // 5. 主题模式
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic.exchange");
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue("topic.queue1");
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue("topic.queue2");
    }

    @Bean
    public Binding topicBinding1(TopicExchange topicExchange, Queue topicQueue1) {
        return BindingBuilder.bind(topicQueue1).to(topicExchange).with("*.orange.*");
    }

    @Bean
    public Binding topicBinding2(TopicExchange topicExchange, Queue topicQueue2) {
        return BindingBuilder.bind(topicQueue2).to(topicExchange).with("*.*.rabbit");
    }
}
```

现在,让我们为每种模式创建监听器:

```java
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListeners {

    // 1. 简单模式监听器
    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        System.out.println("Received message from simple queue: " + message);
    }

    // 2. 工作队列模式监听器
    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue1(String message) {
        System.out.println("Work Queue Listener 1 received: " + message);
    }

    @RabbitListener(queues = "work.queue")
    public void listenWorkQueue2(String message) {
        System.out.println("Work Queue Listener 2 received: " + message);
    }

    // 3. 发布/订阅模式监听器
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String message) {
        System.out.println("Fanout Queue 1 received: " + message);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String message) {
        System.out.println("Fanout Queue 2 received: " + message);
    }

    // 4. 路由模式监听器
    @RabbitListener(queues = "direct.queue1")
    public void listenDirectQueue1(String message) {
        System.out.println("Direct Queue 1 (error) received: " + message);
    }

    @RabbitListener(queues = "direct.queue2")
    public void listenDirectQueue2(String message) {
        System.out.println("Direct Queue 2 (info) received: " + message);
    }

    // 5. 主题模式监听器
    @RabbitListener(queues = "topic.queue1")
    public void listenTopicQueue1(String message) {
        System.out.println("Topic Queue 1 (*.orange.*) received: " + message);
    }

    @RabbitListener(queues = "topic.queue2")
    public void listenTopicQueue2(String message) {
        System.out.println("Topic Queue 2 (*.*.rabbit) received: " + message);
    }
}
```

这样,我们就完成了RabbitMQ的配置和监听器的创建。让我解释一下每种模式的配置:

1. 简单模式: 只需创建一个队列。
2. 工作队列模式: 创建一个队列,但有多个监听器。
3. 发布/订阅模式: 创建一个Fanout交换机和多个队列,并将队列绑定到交换机。
4. 路由模式: 创建一个Direct交换机和多个队列,使用不同的路由键将队列绑定到交换机。
5. 主题模式: 创建一个Topic交换机和多个队列,使用带通配符的主题将队列绑定到交换机。


每个监听器都使用`@RabbitListener`注解,指定要监听的队列。当消息到达队列时,相应的方法会被调用来处理消息。

要使用这些配置,您需要在您的应用程序中注入`RabbitTemplate`,并使用它来发送消息到不同的交换机或队列。例如:

```java
@Autowired
private RabbitTemplate rabbitTemplate;

// 发送简单消息
rabbitTemplate.convertAndSend("simple.queue", "Hello, Simple Queue!");

// 发送到Fanout交换机
rabbitTemplate.convertAndSend("fanout.exchange", "", "Hello, Fanout Exchange!");

// 发送到Direct交换机
rabbitTemplate.convertAndSend("direct.exchange", "error", "This is an error message");

// 发送到Topic交换机
rabbitTemplate.convertAndSend("topic.exchange", "quick.orange.rabbit", "Hello, Topic Exchange!");
```

这样,您就可以在Spring Boot应用程序中使用RabbitMQ的各种消息模式了。