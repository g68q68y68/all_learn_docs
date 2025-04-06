# sentinel教程



下面这份文档旨在较为系统、详细地介绍如何在 Spring Cloud 或者 Spring Boot 环境下使用阿里巴巴的 **Sentinel** 来实现熔断和限流，包括以下几个部分：

1. **Sentinel 基本原理简介**
2. **Sentinel 的依赖与基本接入**
3. **Sentinel 控制台（Dashboard）使用**
4. **注解 @SentinelResource 的配置与示例**
5. **本项目（A 项目）内的熔断限流示例**
6. **调用外部服务（B 项目）的熔断限流示例**
7. **常见问题与注意事项**

文档中会尽量区分**在项目 A 自身的资源熔断/限流**与**项目 A 调用项目 B 时对 B 的限流/熔断**之间的差别，帮助大家在实际使用中更好地理解和配置。

------

## 1. Sentinel 基本原理简介

**Sentinel** 是阿里巴巴开源的一款高可用流量防护产品，其核心功能是对请求进行实时监控、流量控制（限流）、熔断降级（熔断）以及系统负载保护。简单来说，Sentinel 能够针对微服务接口或者业务方法：

- 统计流量、响应时间、线程数等指标
- 根据预先配置的规则自动进行限流或者熔断
- 在触发条件时执行自定义的降级、熔断处理逻辑

Sentinel 的主要概念包括：

- **资源（Resource）**：受 Sentinel 保护的业务逻辑片段，一般可以是一个业务接口、一个方法或一个外部资源。
- **Entry**：对资源进行保护的过程，通过 `SphU.entry("resourceName")` 或者注解 `@SentinelResource("resourceName")` 进行定义。
- **规则（Rule）**：例如流量控制规则、熔断降级规则、系统规则等，用于在流量、响应延迟、异常比率等指标超出阈值时对资源进行保护。

Sentinel 同时还提供了**控制台（Dashboard）**，可以实时地查看流量监控、添加和修改规则，非常方便。

------

## 2. Sentinel 的依赖与基本接入

### 2.1 引入依赖

如果你是 Spring Cloud Alibaba 生态，通常可以直接引入如下依赖（以 Maven 为例）：

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    <version>2.2.10.RELEASE</version> <!-- 版本可根据实际情况调整 -->
</dependency>
```

或者在纯 Spring Boot 项目中，可以使用：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>1.8.5</version> <!-- 版本示例，可根据实际情况调整 -->
</dependency>
```

### 2.2 在配置文件中启用 Sentinel

如果使用 Spring Cloud Alibaba，通常只要在 `application.yml` 或 `application.properties` 中增加一些基础配置（比如是否开启 Sentinel、是否连接远程的 Sentinel 控制台等），示例：

```yml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: 127.0.0.1:8080  # Sentinel Dashboard 启动后所对应的地址:端口
        port: 8719                # 默认情况下 Sentinel Client 侧暴露的端口
```

### 2.3 启动类上开启 Sentinel

在 Spring Boot 启动类中，可以通过 `@EnableSentinel`（在 Spring Cloud Alibaba 中通常不需要显式加，也可以直接使用 `@SpringBootApplication` 即可）或引入后自动配置完成。通常情况下只要引入了依赖，Spring Cloud Alibaba 就会帮你初始化 Sentinel。

------

## 3. Sentinel 控制台（Dashboard）使用

### 3.1 下载并启动 Sentinel Dashboard

- [GitHub - alibaba/Sentinel](https://github.com/alibaba/Sentinel/releases) 可以在 Release 中找到对应版本的 Dashboard 的 jar 包（如 `sentinel-dashboard-xxx.jar`）。
- 在本地或服务器上通过命令 `java -jar sentinel-dashboard-xxx.jar` 运行。默认启动端口为 `8080`，可自行修改。

### 3.2 Sentinel 控制台关键功能

1. **实时监控**
    选择对应的应用名称后，可以看到各资源的实时 QPS、RT、线程数、异常数等。
2. **流量控制**
    添加流控规则：资源名、限流模式（QPS 或线程数）、限流效果（快速失败、排队等）、阈值大小等。
3. **熔断降级**
    添加熔断降级规则：针对资源的响应时间、异常比例、异常数等进行熔断处理，以及熔断时长等。
4. **热点规则**
    针对带参数的接口或方法进行热点参数限流。
5. **系统规则**
    针对整体应用维度的 CPU、Load、内存等进行保护。

添加或更新规则后，Sentinel 客户端会实时同步并生效（需要确保应用已正确接入 Sentinel 控制台的地址、端口）。

> **注意**：在实际的生产环境中，通常会将 Sentinel Dashboard 部署在内网，并结合 Nacos、Apollo 等配置中心的方式来统一管理规则。

------

## 4. 注解 @SentinelResource 的配置与示例

Sentinel 允许通过**注解**的方式来定义“资源”。只要在方法上加上 `@SentinelResource("resourceName")`，就能将该方法纳入 Sentinel 的保护范围。

常用注解配置示例如下：

```java
@RestController
public class TestController {

    /**
     * 基本用法：在方法上添加 @SentinelResource 注解
     */
    @GetMapping("/hello")
    @SentinelResource(
        value = "helloResource",                 // 资源名称
        blockHandler = "helloBlockHandler",      // 被流控或降级时的处理函数
        fallback = "helloFallback"               // 出现异常时的兜底函数
    )
    public String hello() {
        // 这里是核心业务逻辑
        return "Hello Sentinel!";
    }

    /**
     * blockHandler 函数，注意：必须是 public、返回类型与原函数一致，并且参数类型需要与原函数一致(或在最后加一个 BlockException)
     */
    public String helloBlockHandler(BlockException e) {
        // 当流量被限流或熔断时，会进入到这里
        return "【流控或熔断触发】请稍后重试";
    }

    /**
     * fallback 函数，注意：必须与原函数返回类型一致
     * 用于处理业务代码抛出异常时的兜底返回
     */
    public String helloFallback(Throwable t) {
        // 当执行方法内部抛出异常时，会进入这里
        return "【异常兜底】服务出现异常";
    }
}
```

- `value`：Sentinel 资源名，**在 Sentinel 控制台规则配置的时候就会用到这个名字**。
- `blockHandler`：在被限流、被熔断或被系统保护时的处理方法，不会进入原函数执行。
- `fallback`：只要原方法抛出异常，就会进入 fallback 方法，执行兜底逻辑。

此时，你就可以在 **Sentinel 控制台**的“**流量控制**”或“**降级**”等页面，针对 `helloResource` 这个资源名添加相应的规则，来进行限流或熔断保护。

------

## 5. 本项目（A 项目）内的熔断与限流示例

通常情况下，我们需要先搞清楚我们要保护哪些资源。若资源是在本项目 A 内部的接口或方法，那就如下处理：

1. **注解或编程式定义资源**

   - 方式一：在 Controller 层或 Service 层的关键方法上加 `@SentinelResource("xxx")`

   - 方式二：手动使用 Sentinel API：

     ```java
     try (Entry entry = SphU.entry("someResource")) {
         // 资源逻辑
     } catch (BlockException ex) {
         // 被限流或熔断时的处理
     }
     ```

2. **在 Sentinel 控制台添加规则**

   - 进入控制台后，选择应用为“**A 项目**”。

   - 在“

     流量控制

     ”中，新增规则：

     - 资源名：与上一步中定义的 `"xxx"` 或 `"someResource"` 保持一致
     - 阈值类型：QPS 或并发量
     - 阈值大小：具体数字
     - 流控行为：直接拒绝或排队等待

   - 在“

     熔断降级

     ”中，新增规则：

     - 资源名：同上
     - 熔断类型：如平均响应时间 / 异常比例 / 异常数
     - 阈值大小和时间窗口

3. **验证**

   - 向该接口发起大量请求或模拟异常
   - 在控制台观察实时监控面板
   - 当超过阈值或满足熔断条件时，请求会走 blockHandler / fallback 逻辑

**场景示例**：

- **限流**：限制 A 项目自身的某个接口的 QPS 不超过 10。
- **熔断**：如果 A 项目自身的某个接口异常比例大于 50% 连续 1 分钟，则触发熔断，熔断时长 10 秒。

------

## 6. 调用外部服务（B 项目）的熔断与限流示例

在微服务环境下，A 项目需要调用 B 项目的接口，这里通常有两种思路：

1. **在 A 项目中，对“调用 B 的接口”这个过程设置资源保护规则**
   - 即 **对 A 发起的 Feign/RestTemplate 调用** 进行 Sentinel 保护。只要这段调用被认定为一个 Sentinel Resource，就可以在 A 项目侧配置流控与熔断。
   - 作用是：如果调用 B 的 QPS 过高，或者调用 B 出现大量错误或超时，A 这边可以先行限流、熔断，不要一直调用 B，从而避免导致 B 被压垮，也避免 A 自身线程被占满。
2. **在 B 项目中，B 自身也使用 Sentinel**
   - B 的服务端接收到调用时，对 B 的资源进行保护，比如 B 项目控制了 B 的某些接口的流量或熔断策略。
   - 当 B 自身的流量超过阈值时，B 会直接返回限流或降级结果。这样做是为了保护 B 自己。

> 二者并不冲突。通常来说：**A 项目保护调用过程**，B 项目保护其自身接口逻辑。

### 6.1 A 项目对 Feign/RestTemplate 调用进行限流/熔断

#### 6.1.1 对 Feign 接口进行保护

如果在 A 项目中使用 **OpenFeign** 调用 B 的接口，可以结合 **Sentinel Feign** 来做保护。

1. **引入依赖**：

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
       <!-- 版本与 Spring Cloud Alibaba 相匹配 -->
   </dependency>
   ```

2. **在 FeignClient 上配置 fallback 或 blockHandler**

   - Spring Cloud Alibaba 提供了 **Feign Sentinel** 的增强，可以给 FeignClient 配置 fallback 类，当发生被限流/熔断异常时走降级逻辑。

   - 需要在 

     ```
     application.yml
     ```

      中增加：

     ```yml
     feign:
       sentinel:
         enabled: true
     ```

   - 在接口上加注解 `@FeignClient(value = "B-SERVICE", fallback = XXXFallback.class)`，然后在 `XXXFallback` 中实现对应接口，并进行降级处理。

3. **在 Sentinel 控制台中，找到对应的 Feign Resource**

   - Sentinel 默认会给 Feign 调用自动生成资源名，或在自定义配置中对该调用做命名，然后设置规则即可。

如果你想显式地使用注解来标注资源，也可以用以下方式包裹 Feign 调用：

```java
@Service
public class BServiceCaller {

    @Resource
    private BFeignClient bFeignClient;

    @SentinelResource(value = "callBResource", blockHandler = "callBBlockHandler", fallback = "callBFallback")
    public String callB() {
        return bFeignClient.someMethod();
    }

    public String callBBlockHandler(BlockException ex) {
        // 被限流或熔断时处理
        return "【限流或熔断】访问B被阻止";
    }

    public String callBFallback(Throwable ex) {
        // 业务异常时处理
        return "【调用B发生异常】已降级";
    }
}
```

#### 6.1.2 对 RestTemplate 调用进行保护

如果使用的是 RestTemplate：

- 可以通过手动 API 或注解的方式，将 “RestTemplate 调用 B” 这一段逻辑包装为一个资源。

- 示例：

  ```java
  @Service
  public class BServiceCaller {
  
      @Autowired
      private RestTemplate restTemplate;
  
      @SentinelResource(value = "callBWithRest", blockHandler = "block", fallback = "fallback")
      public String callBWithRest() {
          String url = "http://B-SERVICE/bSomeApi";
          // 这时假设已经在Spring Cloud环境下，通过注册中心映射 B-SERVICE
          return restTemplate.getForObject(url, String.class);
      }
  
      public String block(BlockException ex) {
          return "【限流或熔断】无法访问B";
      }
  
      public String fallback(Throwable ex) {
          return "【异常降级】B服务异常或网络错误";
      }
  }
  ```

- 在控制台即可针对 “callBWithRest” 这个资源名，配置流控或熔断规则。

### 6.2 B 项目自身对接口进行限流/熔断

B 项目可以和 A 项目一样接入 Sentinel，保护自己的业务：

1. 在 B 项目中引入 Sentinel 依赖，配置好 `application.yml` 连接 Sentinel Dashboard。
2. 在 B 的核心接口或关键方法上加 `@SentinelResource` 或使用 API 的方式定义资源。
3. 在 B 的 Sentinel 控制台页面，针对这些资源设置流控、熔断规则。
4. 当 A 或其他服务调用 B 时，如果 B 的流量过大或 B 自身出错率很高，B 就会自动进行限流或熔断，返回限流/熔断的结果（通常是 429、降级信息等）。

这样就实现了**双重保护**：

- **A 项目**：保护自己在调用 B 时不要被拖垮；或者当 A 接口自身的 QPS 过高时在自己这边先限流、降级。
- **B 项目**：保护自身服务，当压力过大或者出现故障时主动限流/熔断。

------

## 7. 常见问题与注意事项

1. 本地开发环境与生产环境的配置差异
   - 开发时可以先在本地启动 Sentinel Dashboard，A/B 项目都连到本地 Dashboard 测试。
   - 生产环境需要将 Dashboard 独立部署，并在项目配置文件中指向正确的 Dashboard 地址以及端口。
2. 资源名的确定
   - 在使用 `@SentinelResource` 时，一定要写好 `value`，并且在控制台配置规则的时候要对应同一个名字。
3. blockHandler 与 fallback 的区别
   - blockHandler 用于 Sentinel 触发限流、熔断时的处理，**不会调用原函数**；
   - fallback 用于代码本身抛异常时的兜底逻辑，**原函数会执行，抛出异常才会进入 fallback**。
4. 如果想在一个方法里区分参数来限流
   - 可以使用 Sentinel 的热点限流规则，需要在代码里调用 `SphU.entry("resourceName", EntryType.IN, 1, args)` 或者使用注解配合切面，但一般需要在控制台中开启“**热点规则**”来配置。
5. 微服务多实例如何配置？
   - 每个实例都会注册到同一个 Sentinel Dashboard，只要应用名相同即可。
   - 规则可以设置为单机还是集群生效。若需要更加灵活的规则下发，可以结合 Nacos、Apollo 等做动态配置管理。

------

## 小结

- **本项目 A 内部的熔断/限流**：只需在 A 中对接口或方法定义 Sentinel 资源，然后在 A 项目的 Sentinel 控制台中添加流量规则/降级规则即可。
- **A 项目调用 B 项目（对外部服务调用的熔断/限流）**：本质上是对 A 侧的资源（Feign 调用、RestTemplate 调用）进行保护，也可以在 B 项目本身再做一层保护。
- 常用注解 `@SentinelResource` 可以快速设置 blockHandler、fallback 等兜底处理。
- 生产环境中要注意多实例场景以及统一配置中心的管理方式。

以上就是关于 **Sentinel** 的使用教程、控制台页面配置方法以及在代码中注解配置的方法，并着重区分了**项目 A 自己的资源保护**和**调用外部服务（B 项目）的保护**两种不同场景的配置方法。希望能帮助你更好地理解并应用 Sentinel 的熔断与限流特性。