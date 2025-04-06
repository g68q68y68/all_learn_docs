# Spring Boot中缓存(Cache)

## 1. 引入Spring Boot Cache

首先,要在Spring Boot项目中使用缓存,需要添加相关依赖。在`pom.xml`文件中添加以下依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

然后,在主应用类上添加`@EnableCaching`注解来启用缓存支持:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 2. 缓存注解及其用法

Spring Boot提供了几个主要的缓存注解,下面我们逐一讲解:

#### 2.1 @Cacheable

`@Cacheable`注解用于将方法的返回结果缓存起来。当再次使用相同的参数调用该方法时,会直接从缓存中返回结果,而不是重新执行方法。

示例:

```java
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        System.out.println("Fetching user from database...");
        // 模拟从数据库获取用户
        return new User(id, "User " + id);
    }
}
```

在这个例子中,`getUserById`方法的结果会被缓存在名为"users"的缓存中,缓存的key是方法参数`id`。

#### 2.2 @CachePut

`@CachePut`注解用于更新缓存。与`@Cacheable`不同,被`@CachePut`注解的方法总是会被执行,其结果会被放入缓存中。

示例:

```java
@CachePut(value = "users", key = "#user.id")
public User updateUser(User user) {
    System.out.println("Updating user in database...");
    // 模拟更新用户信息
    return user;
}
```

这个方法会更新"users"缓存中对应用户ID的缓存内容。

#### 2.3 @CacheEvict

`@CacheEvict`注解用于从缓存中移除一个或多个缓存条目。

示例:

```java
@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    System.out.println("Deleting user from database...");
    // 模拟从数据库删除用户
}
```

这个方法会从"users"缓存中移除指定ID的用户缓存。

#### 2.4 @Caching

`@Caching`注解允许在同一个方法上组合多个缓存操作。

示例:

```java
@Caching(evict = {
    @CacheEvict(value = "users", key = "#user.id"),
    @CacheEvict(value = "usersByName", key = "#user.name")
})
public void updateUserInfo(User user) {
    System.out.println("Updating user info and clearing caches...");
    // 更新用户信息
}
```

这个方法会同时从"users"和"usersByName"两个缓存中移除相关的缓存条目。

#### 2.5 @CacheConfig

`@CacheConfig`是一个类级别的注解,用于简化该类中所有缓存操作的配置。

示例:

```java
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = {"users"})
public class UserService {

    @Cacheable(key = "#id")
    public User getUserById(Long id) {
        System.out.println("Fetching user from database...");
        return new User(id, "User " + id);
    }

    @Cacheable(key = "#name")
    public User getUserByName(String name) {
        System.out.println("Fetching user by name from database...");
        return new User(1L, name);
    }
}
```

在这个例子中,`@CacheConfig(cacheNames = {"users"})`指定了该类中所有缓存操作默认使用的缓存名称,这样就不需要在每个方法上重复指定`value`属性。

### 3. 缓存配置

Spring Boot默认使用`ConcurrentMapCacheManager`作为缓存提供者,这对于开发和测试环境来说足够了。但在生产环境中,你可能需要使用更强大的缓存解决方案,如Redis或EhCache。

以Redis为例,首先添加Redis依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

然后在`application.properties`或`application.yml`中配置Redis:

```yaml
spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```

这样,Spring Boot就会自动配置`RedisCacheManager`作为缓存提供者。

### 4. 注意事项

1. 缓存的key：可以使用SpEL表达式来定义复杂的key。
2. 缓存的条件：可以使用`condition`属性来指定缓存的条件。
3. 缓存的同步：在多线程环境下,可以使用`sync = true`来确保同一个key只有一个线程在计算。
4. 缓存的过期时间：不同的缓存提供者有不同的配置方式,通常需要在缓存管理器中配置。


使用Spring Boot的缓存系统可以显著提高应用程序的性能,特别是对于计算密集型或I/O密集型的操作。但也要注意合理使用缓存,避免缓存数据不一致或占用过多内存的问题。









## 2. AOP的基本概念

AOP(Aspect-Oriented Programming)是一种编程范式,它通过将横切关注点(cross-cutting concerns)从主业务逻辑中分离出来,以提高代码的模块化程度。在Spring中,AOP主要用于：

- 日志记录
- 性能监控
- 事务管理
- 安全控制
- 错误处理


### 2. 在Spring Boot中引入AOP

要在Spring Boot项目中使用AOP,首先需要添加相关依赖。在`pom.xml`文件中添加以下依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

添加这个依赖后,Spring Boot会自动配置AOP支持。

### 3. AOP的主要注解及其用法

Spring AOP使用注解来定义切面、切点和通知。以下是主要的注解：

1. `@Aspect`: 声明一个类为切面
2. `@Pointcut`: 定义一个切点
3. `@Before`: 前置通知,在目标方法执行前执行
4. `@After`: 后置通知,在目标方法执行后执行(无论是否抛出异常)
5. `@AfterReturning`: 返回通知,在目标方法成功执行后执行
6. `@AfterThrowing`: 异常通知,在目标方法抛出异常后执行
7. `@Around`: 环绕通知,在目标方法执行前后都执行


### 4. 实际应用示例

让我们通过一个实际的例子来说明如何使用这些注解。我们将创建一个切面来记录方法的执行时间。

首先,创建一个切面类:

```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");

        return proceed;
    }

    @Before("serviceMethods()")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        System.out.println("Executing: " + joinPoint.getSignature());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("Method " + joinPoint.getSignature() + " returned: " + result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        System.out.println("Method " + joinPoint.getSignature() + " threw: " + error);
    }
}
```

然后,创建一个服务类来测试我们的切面:

```java
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getUserName(Long id) {
        // 模拟数据库操作
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "User " + id;
    }

    public void createUser(String name) {
        // 模拟创建用户
        System.out.println("Creating user: " + name);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
    }
}
```

最后,在主应用类中测试:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        UserService userService = context.getBean(UserService.class);

        // 测试正常执行
        String userName = userService.getUserName(1L);
        System.out.println("User name: " + userName);

        // 测试异常情况
        try {
            userService.createUser("");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
    }
}
```

运行这个应用,你将看到AOP切面记录的日志信息,包括方法执行时间、方法调用前的日志、方法返回值,以及异常情况。

### 5. 注意事项

1. 切点表达式：学习和正确使用切点表达式非常重要,它决定了哪些方法会被切面拦截。
2. 代理机制：Spring AOP使用代理模式实现。默认情况下,如果使用接口,Spring会使用JDK动态代理；如果没有接口,会使用CGLIB。
3. 自调用问题：当一个bean方法调用其自身的另一个方法时,这个调用不会被AOP拦截。
4. 性能考虑：虽然AOP非常强大,但过度使用可能会影响性能。应该谨慎使用,特别是在性能敏感的应用中。
5. 顺序：当多个切面应用到同一个连接点时,可以使用`@Order`注解来控制它们的执行顺序。
6. 异常处理：在使用`@AfterThrowing`时,要注意异常的处理方式,确保不会吞掉重要的异常信息。


通过合理使用AOP,我们可以实现更清晰、更模块化的代码结构,提高代码的可维护性和可重用性。但同时也要注意合理使用,避免过度复杂化你的应用程序。