# nacos教程



### Nacos 使用教程：作为配置中心和服务发现中心

**Nacos** 是阿里巴巴开源的一个动态服务发现、配置管理和服务管理平台，常用于微服务架构中。在微服务场景下，Nacos 可以充当服务发现中心、配置中心、动态 DNS 和动态配置管理中心等角色。

以下是关于 Nacos 的使用教程，涵盖了作为配置中心和服务发现中心的使用，解释了相关概念，并介绍了配置数据和 Sentinel 数据的持久化。

------

## 1. Nacos 简介

Nacos 是一个开源的动态服务发现、配置管理和服务管理平台。它的功能包括：

- **服务发现和服务管理**：可以帮助微服务系统进行服务注册与发现。
- **动态配置管理**：可以帮助管理应用的配置信息。
- **动态 DNS 和负载均衡**：为微服务提供DNS支持和负载均衡功能。

Nacos 提供了两个核心功能：**配置中心**和**服务发现**。

------

## 2. 配置中心

### 2.1 Nacos 配置中心简介

配置中心是集中管理应用程序的所有配置数据。Nacos 配置中心主要负责以下任务：

- **集中管理配置文件**：将应用程序的配置信息统一保存在 Nacos 中，并允许在运行时动态修改配置。
- **动态推送配置变更**：当配置发生变化时，Nacos 可以通过推送机制（如长轮询）及时通知客户端应用更新配置。
- **版本管理**：支持多版本配置，可以回滚到历史版本。

### 2.2 使用 Nacos 配置中心

#### 2.2.1 启动 Nacos 服务

1. 下载 Nacos（[Nacos GitHub](https://github.com/alibaba/nacos)）。

2. 解压并进入 Nacos 目录，启动 Nacos 服务：

   ```bash
   # 启动 Nacos
   sh startup.sh -m standalone  # 启动单机模式
   ```

   启动后，Nacos 默认启动在 `8848` 端口，可以在浏览器中访问 `http://127.0.0.1:8848/nacos`。

#### 2.2.2 配置管理

1. **添加配置**：在 Nacos 控制台，进入 **配置管理** > **配置列表**，点击 **新增配置**。填写 **Data ID**、**Group** 和 **配置内容**。
2. **配置内容格式**：可以使用 `properties`、`yaml`、`json` 等格式。
3. **发布配置**：点击发布配置，配置会被保存到 Nacos 中并生效。

#### 2.2.3 客户端集成

1. **Spring Cloud Nacos 配置中心集成**

   引入依赖：

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
   </dependency>
   ```

2. **在 `application.yml` 中配置 Nacos 配置源**

   ```yaml
   spring:
     cloud:
       nacos:
         config:
           server-addr: 127.0.0.1:8848  # Nacos 服务地址
           file-extension: yaml          # 配置文件格式
           shared-configs:
             - dataid: example.yaml      # 配置的 DataID
               group: DEFAULT_GROUP     # 配置的 Group
   ```

3. **自动加载配置**：Spring Boot 应用启动后，会自动从 Nacos 配置中心加载配置信息，进行注入。

   ```java
   @Value("${example.property}")
   private String exampleProperty;
   ```

4. **监听配置变化**：可以使用 `@NacosValue` 注解来监听 Nacos 配置的变化，配置变更时会自动更新。

------

## 3. 服务发现中心

### 3.1 Nacos 服务发现简介

服务发现是微服务架构中的关键组成部分，Nacos 提供了服务注册和发现的功能。服务发现的基本流程是：微服务将自己的服务实例注册到服务发现中心，其他微服务通过服务发现中心查找目标服务实例。

### 3.2 使用 Nacos 服务发现

#### 3.2.1 服务注册

1. 在 Spring Cloud 项目中，使用 `spring-cloud-starter-alibaba-nacos-discovery` 来实现服务注册。

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
   </dependency>
   ```

2. 在 `application.yml` 中配置服务注册：

   ```yaml
   spring:
     cloud:
       nacos:
         discovery:
           server-addr: 127.0.0.1:8848  # Nacos 服务地址
           namespace: public            # 指定命名空间
   ```

3. 服务启动时，会自动将服务注册到 Nacos。可以在 Nacos 控制台的 **服务管理** 中查看到已注册的服务。

#### 3.2.2 服务发现

1. 其他微服务可以通过 Nacos 服务发现来获取到已注册的服务信息。

2. 使用 Feign 或 RestTemplate 来调用其他服务：

   ```java
   @FeignClient(name = "other-service")
   public interface OtherServiceClient {
       @GetMapping("/api")
       String callOtherService();
   }
   ```

3. Nacos 会在后台根据 `other-service` 的服务名进行动态查找，获得可用实例并进行调用。

------

## 4. 配置数据持久化

### 4.1 Nacos 配置持久化

Nacos 默认会将配置数据持久化到数据库中。每当配置变更时，都会更新数据库中的记录。

1. **数据库支持**：Nacos 支持 MySQL、PostgreSQL 等数据库，可以配置不同的数据库进行持久化。
2. **配置中心的持久化**：当你新增、修改、删除配置时，这些配置信息会存储在数据库中，确保配置数据不会丢失。可以通过数据库备份来进行配置数据的备份。

#### 4.1.1 配置 Nacos 使用 MySQL 持久化

1. 修改 Nacos 的配置文件，找到 `application.properties`，并配置数据库连接。

   ```properties
   # 配置数据库类型为 MySQL
   spring.datasource.platform=mysql
   
   # 数据库连接配置
   spring.datasource.url=jdbc:mysql://127.0.0.1:3306/nacos_config?useUnicode=true&characterEncoding=UTF-8
   spring.datasource.username=root
   spring.datasource.password=123456
   ```

2. 运行 Nacos，数据将会持久化到 MySQL 中。

------

## 5. Sentinel 数据持久化

### 5.1 Sentinel 数据持久化概述

Sentinel 是一款流量控制组件，它的配置数据（例如限流规则、熔断规则）默认是保存在内存中的。为了应对服务重启时数据丢失的问题，我们可以配置 Sentinel 数据持久化，将流量控制规则保存在外部存储（如数据库、文件系统）中。

### 5.2 Sentinel 数据持久化配置

#### 5.2.1 使用 Nacos 持久化 Sentinel 数据

1. **引入 Sentinel Nacos 支持**：

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud</groupId>
       <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
   </dependency>
   ```

2. **配置 Nacos 为 Sentinel 数据源**：

   ```yaml
   spring:
     cloud:
       nacos:
         config:
           server-addr: 127.0.0.1:8848
           data-id: sentinel-rules  # Nacos 中配置数据的 Data ID
           group: DEFAULT_GROUP    # 配置的 Group
       sentinel:
         transport:
           dashboard: 127.0.0.1:8080  # Sentinel 控制台地址
   ```

3. **持久化 Sentinel 流量控制规则**：在 Nacos 中创建 Data ID 为 `sentinel-rules` 的配置，添加流量控制、熔断降级规则。

4. **从 Nacos 加载规则**：当服务启动时，Sentinel 会自动从 Nacos 中拉取这些配置，并将它们应用到当前服务中。

#### 5.2.2 使用数据库持久化 Sentinel 数据

1. **配置数据库**：可以将 Sentinel 的规则持久化到数据库中，例如 MySQL、PostgreSQL 等。
2. **引入数据库持久化相关配置**：根据使用的数据库，配置数据库连接信息，创建相应的表格来存储 Sentinel 规则数据。

------

## 6. 总结

1. **Nacos 作为配置中心**：它集中管理应用配置，支持动态配置更新，保证配置信息的实时同步，并支持版本管理。
2. **Nacos 作为服务发现中心**：通过服务注册与发现，微服务可以动态地查询和调用其他服务，实现微服务之间的通信。
3. **配置数据持久化**：通过数据库（如 MySQL）实现配置数据的持久化，确保数据不会丢失。
4. **Sentinel 数据持久化**：通过 Nacos 或数据库等外部存储系统，将 Sentinel 的流量控制、熔断降级规则进行持久化，避免服务重启后丢失配置。

通过结合使用 Nacos 和 Sentinel，可以实现微服务的动态配置管理、流量控制和高可用性。