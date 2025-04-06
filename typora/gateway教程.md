# gateway教程

### Spring Cloud Gateway 使用教程

**Spring Cloud Gateway** 是一个用于构建 API 网关的框架，基于 Spring WebFlux 提供的异步、非阻塞支持。它的主要功能包括路由请求、过滤请求、负载均衡、限流、熔断、认证授权等。

本文将详细介绍 Spring Cloud Gateway 的使用教程，包括核心概念、常见功能配置，并配有相关注释说明。

------

## 1. Spring Cloud Gateway 基本概念

- **路由（Route）**：指的是请求匹配规则，可以配置请求的 URL、方法、头信息等，指向具体的后端服务。
- **过滤器（Filter）**：用于请求处理前后进行处理，例如请求修改、响应修改、鉴权、日志等。
- **网关（Gateway）**：作为前置服务，负责接受请求并转发到具体的服务（即微服务）。

------

## 2. 添加 Spring Cloud Gateway 依赖

首先，确保你的 Spring Boot 项目中包含了 `spring-cloud-starter-gateway` 依赖。可以通过 Maven 来引入该依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

你还需要引入 Spring Cloud 的 BOM（Bill of Materials）来确保依赖版本的兼容性：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2020.0.3</version> <!-- 具体版本根据需求调整 -->
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后，你可以在 Spring Boot 的主类中添加 `@EnableGateway` 注解来启用 Spring Cloud Gateway：

```java
@SpringBootApplication
@EnableGateway
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

------

## 3. 配置 Spring Cloud Gateway

### 3.1 基础配置

在 `application.yml` 文件中进行基础配置，设置网关服务的端口以及默认路由的配置。

```yaml
server:
  port: 8080  # 网关服务的端口，客户端请求都会首先经过这个端口

spring:
  cloud:
    gateway:
      routes:
        # 配置路由规则
        - id: example_route
          uri: http://localhost:8081  # 请求转发的目标服务地址
          predicates: 
            - Path=/example/**  # 当请求路径为 /example/** 时进行路由转发
```

上述配置的含义是，当访问网关的 `http://localhost:8080/example/**` 时，Spring Cloud Gateway 会将请求转发到 `http://localhost:8081`。

------

### 3.2 配置多个路由

你可以配置多个路由，进行不同请求路径的转发：

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 路由 1
        - id: route_1
          uri: http://localhost:8081
          predicates:
            - Path=/service1/**
        
        # 路由 2
        - id: route_2
          uri: http://localhost:8082
          predicates:
            - Path=/service2/**
        
        # 路由 3
        - id: route_3
          uri: http://localhost:8083
          predicates:
            - Path=/service3/**
```

- 请求路径 `/service1/**` 将转发到 `http://localhost:8081`。
- 请求路径 `/service2/**` 将转发到 `http://localhost:8082`。
- 请求路径 `/service3/**` 将转发到 `http://localhost:8083`。

------

### 3.3 配置路径重写

Spring Cloud Gateway 还提供了路径重写功能，允许将请求路径中的某些部分进行修改后再转发到目标服务。

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: path_rewrite_route
          uri: http://localhost:8081
          predicates:
            - Path=/api/**  # 请求路径必须以 /api/ 开头
          filters:
            - RewritePath=/api/(?<segment>.*), /${segment}  # 去掉 /api 前缀后转发
```

在上述配置中，当请求路径为 `/api/**` 时，`RewritePath` 过滤器会将 `/api` 部分去掉，再将请求转发到目标服务。

------

### 3.4 配置负载均衡

Spring Cloud Gateway 内置了对 Spring Cloud LoadBalancer 的支持，可以对目标服务实现负载均衡：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: load_balanced_route
          uri: lb://SERVICE-NAME  # 使用服务名进行负载均衡
          predicates:
            - Path=/service/**  # 请求路径必须以 /service/** 开头
```

此时，`lb://SERVICE-NAME` 会根据配置的负载均衡策略从注册中心获取服务实例，并将请求转发到相应的实例。

------

## 4. 配置过滤器

Spring Cloud Gateway 提供了多种过滤器，可以用于请求处理过程中的各种场景。过滤器分为两类：**全局过滤器** 和 **路由过滤器**。

### 4.1 路由过滤器

路由过滤器通常用于特定路由的请求处理中，可以在 `application.yml` 中进行配置：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: route_with_filter
          uri: http://localhost:8081
          predicates:
            - Path=/service/**
          filters:
            - AddRequestHeader=X-Request-Foo, Bar  # 添加请求头
            - AddResponseHeader=X-Response-Foo, Bar  # 添加响应头
            - StripPrefix=1  # 去掉路径前缀 `/service`
```

- `AddRequestHeader`：添加请求头。
- `AddResponseHeader`：添加响应头。
- `StripPrefix`：去掉请求路径前缀。

### 4.2 全局过滤器

全局过滤器是应用于所有路由的过滤器。可以通过实现 `GlobalFilter` 接口来创建自定义的全局过滤器。

```java
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 在请求处理之前做一些事情
        System.out.println("Global Filter: Pre Filter");

        // 继续执行请求
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 在响应返回之后做一些事情
            System.out.println("Global Filter: Post Filter");
        }));
    }

    @Override
    public int getOrder() {
        return -1;  // 过滤器执行顺序，数字越小越先执行
    }
}
```

- `filter` 方法用于处理请求和响应。
- `getOrder` 方法用于设置过滤器的执行顺序，值越小越优先执行。

------

### 4.3 过滤器的使用场景

- **请求日志记录**：可以记录每个请求的路径、请求方法等。
- **认证与授权**：通过过滤器可以检查请求头中的认证信息，进行鉴权。
- **限流与熔断**：可以通过过滤器实现请求的限流或熔断策略。

------

## 5. 配置认证和授权

Spring Cloud Gateway 支持使用 **OAuth2** 或 **JWT** 来进行认证和授权。

### 5.1 配置基于 OAuth2 的认证

首先，确保你有 OAuth2 提供者（如 Keycloak、Auth0 等）。

在 `application.yml` 中配置 OAuth2 客户端信息：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: oauth2_route
          uri: http://localhost:8081
          predicates:
            - Path=/secure/**
          filters:
            - TokenRelay  # 使用 OAuth2 进行令牌传递
    security:
      oauth2:
        client:
          registration:
            oauth2:
              client-id: my-client-id
              client-secret: my-client-secret
              provider: my-oauth-provider
          provider:
            my-oauth-provider:
              authorization-uri: http://auth-server/oauth/authorize
              token-uri: http://auth-server/oauth/token
```

`TokenRelay` 过滤器将 OAuth2 令牌转发到后端服务中，确保目标服务也能识别和验证。

------

## 6. 配置限流与熔断

Spring Cloud Gateway 与 **Resilience4j** 结合使用时，可以很方便地实现限流与熔断策略。

### 6.1 配置限流

Spring Cloud Gateway 提供了基于请求的限流配置，例如限流每秒请求数：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rate_limited_route
          uri: http://localhost:8081
          predicates:
            - Path=/limited/**
          filters:
            - RequestRateLimiter=redis-rate-limiter,1,1  # 限流配置：1秒钟允许1个请求
```

该配置会限制 `/limited/**` 路径每秒只能接受 1 个请求。

### 6.2 配置熔断

Spring Cloud Gateway 与 **Resilience4j** 集成，提供了熔断功能。可以在 `filters` 中配置：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: circuit_breaker_route
          uri: http://localhost:8081
          predicates:
            - Path=/cb/**
          filters:
            - CircuitBreaker=cb, fallback=fallback  # 熔断器，fallback 是熔断时的回退处理方法
```

配置熔断后，如果请求连续失败，系统会触发熔断策略，返回回退的响应。

------

## 7. 总结

1. **路由配置**：Spring Cloud Gateway 允许通过配置文件（如 `application.yml`）来进行路由匹配，支持路径、查询参数等条件。
2. **过滤器配置**：可以使用路由过滤器来对特定路由进行请求头、响应头、路径重写等操作，也可以定义全局过滤器进行全局请求处理。
3. **认证与授权**：Spring Cloud Gateway 支持 OAuth2 和 JWT 等认证方式，可以为接口配置安全策略。
4. **限流与熔断**：通过与 Resilience4j 配合，Spring Cloud Gateway 提供了基于请求数的限流和熔断策略来保护微服务。

这些功能的组合使得 Spring Cloud Gateway 成为构建微服务架构中 API 网关的强大工具。