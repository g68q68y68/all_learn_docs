# spring-boot-starter-quartz使用



使用 spring-boot-starter-quartz，创建 MySQL 数据库，并创建控制器层的接口，以便进行页面上的任务配置。

以下是使用 Spring Boot 和 Quartz 调度器，集成 MySQL，并创建 REST API 用于任务配置的步骤：

1. 项目设置和依赖


首先，创建一个新的 Spring Boot 项目，并在 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-quartz</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

2. MySQL 配置


在 `application.properties` 中配置 MySQL：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quartz_demo
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.initialize-schema=always
```

3. Quartz 配置


创建一个 Quartz 配置类：

```java
package com.example.quartzapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setDataSource(dataSource);
        
        Properties properties = new Properties();
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.useProperties", "true");
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        
        schedulerFactory.setQuartzProperties(properties);
        
        return schedulerFactory;
    }
}
```

4. 任务和触发器类


创建一个示例任务：

```java
package com.example.quartzapp.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class SampleJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("示例任务正在执行...");
    }
}
```

5. 服务层


创建一个管理任务的服务：

```java
package com.example.quartzapp.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleJob(String jobName, String groupName, String cronExpression) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
                .withIdentity(jobName, groupName)
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "Trigger", groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.deleteJob(jobKey);
    }

    public boolean pauseJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
            return true;
        }
        return false;
    }

    public boolean resumeJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
            return true;
        }
        return false;
    }
}
```

6. 控制器层


创建一个 REST 控制器来暴露任务管理端点：

```java
package com.example.quartzapp.controller;

import com.example.quartzapp.service.JobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping
    public ResponseEntity<String> scheduleJob(@RequestParam String jobName,
                                              @RequestParam String groupName,
                                              @RequestParam String cronExpression) {
        try {
            jobService.scheduleJob(jobName, groupName, cronExpression);
            return ResponseEntity.ok("任务调度成功");
        } catch (SchedulerException e) {
            return ResponseEntity.badRequest().body("任务调度失败: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteJob(@RequestParam String jobName,
                                            @RequestParam String groupName) {
        try {
            jobService.deleteJob(jobName, groupName);
            return ResponseEntity.ok("任务删除成功");
        } catch (SchedulerException e) {
            return ResponseEntity.badRequest().body("任务删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/pause")
    public ResponseEntity<String> pauseJob(@RequestParam String jobName,
                                           @RequestParam String groupName) {
        try {
            boolean paused = jobService.pauseJob(jobName, groupName);
            if (paused) {
                return ResponseEntity.ok("任务暂停成功");
            } else {
                return ResponseEntity.badRequest().body("任务未找到");
            }
        } catch (SchedulerException e) {
            return ResponseEntity.badRequest().body("任务暂停失败: " + e.getMessage());
        }
    }

    @PutMapping("/resume")
    public ResponseEntity<String> resumeJob(@RequestParam String jobName,
                                            @RequestParam String groupName) {
        try {
            boolean resumed = jobService.resumeJob(jobName, groupName);
            if (resumed) {
                return ResponseEntity.ok("任务恢复成功");
            } else {
                return ResponseEntity.badRequest().body("任务未找到");
            }
        } catch (SchedulerException e) {
            return ResponseEntity.badRequest().body("任务恢复失败: " + e.getMessage());
        }
    }
}
```

7. 使用示例


现在您可以使用这些端点来管理您的 Quartz 任务。以下是一些使用 curl 命令与 API 交互的示例：

- 调度新任务：


```plaintext
curl -X POST "http://localhost:8080/api/jobs?jobName=myJob&groupName=myGroup&cronExpression=0 0/1 * * * ?"
```

- 删除任务：


```plaintext
curl -X DELETE "http://localhost:8080/api/jobs?jobName=myJob&groupName=myGroup"
```

- 暂停任务：


```plaintext
curl -X PUT "http://localhost:8080/api/jobs/pause?jobName=myJob&groupName=myGroup"
```

- 恢复任务：


```plaintext
curl -X PUT "http://localhost:8080/api/jobs/resume?jobName=myJob&groupName=myGroup"
```

这个设置为管理 Quartz 任务通过 REST API 提供了一个基本结构。您可以通过添加更多任务类型、实现任务参数和创建前端界面来与这些端点交互来扩展此功能。

请记住在运行应用程序之前在 MySQL 中创建 `quartz_demo` 数据库。当您第一次启动应用程序时，Quartz 表将自动创建。

要创建任务配置的 Web 界面，您可以开发一个简单的前端应用程序（使用 React、Angular 或 Vue.js），与这些 REST 端点交互。前端将提供用于创建、更新、删除、暂停和恢复任务的表单，使用户可以通过用户友好的界面轻松管理计划任务。