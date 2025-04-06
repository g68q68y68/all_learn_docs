

# springboot发送http请求



在Spring Boot中，有几种常用的方式来发送HTTP请求。我们将详细介绍以下几种方法：

1. RestTemplate
2. WebClient
3. OpenFeign
4. HttpClient


让我们逐一探讨这些方法：

### 1. RestTemplate

RestTemplate是Spring框架提供的同步HTTP客户端。它是最传统和广泛使用的方法。

#### 特点：

- 同步阻塞式API
- 简单易用
- 支持各种HTTP方法
- 可以轻松处理请求和响应


#### 示例代码：

首先，在你的Spring Boot应用中配置RestTemplate：

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

然后，你可以在服务类中使用RestTemplate：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {

    private final RestTemplate restTemplate;

    @Autowired
    public HttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getExample() {
        String url = "https://api.example.com/data";
        return restTemplate.getForObject(url, String.class);
    }

    public String postExample() {
        String url = "https://api.example.com/data";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String requestBody = "{\"key\":\"value\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }
}
```

### 2. WebClient

WebClient是Spring 5引入的非阻塞式、响应式HTTP客户端。它是RestTemplate的现代替代品。

#### 特点：

- 非阻塞式API
- 支持同步和异步操作
- 响应式编程模型
- 更好的性能和可伸缩性


#### 示例代码：

首先，添加WebFlux依赖到你的pom.xml：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

然后，配置WebClient：

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
```

使用WebClient的服务类：

```java
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ReactiveHttpService {

    private final WebClient webClient;

    public ReactiveHttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.example.com").build();
    }

    public Mono<String> getExample() {
        return webClient.get()
                .uri("/data")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> postExample() {
        String requestBody = "{\"key\":\"value\"}";
        return webClient.post()
                .uri("/data")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}
```

### 3. OpenFeign

OpenFeign是一个声明式的HTTP客户端，它使创建HTTP请求变得非常简单。

#### 特点：

- 声明式API
- 易于使用和维护
- 支持多种序列化器
- 与Spring Cloud集成良好


#### 示例代码：

首先，添加OpenFeign依赖到你的pom.xml：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

在主应用类上启用Feign：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

创建Feign客户端接口：

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "example-api", url = "https://api.example.com")
public interface ExampleApiClient {

    @GetMapping("/data")
    String getData();

    @PostMapping("/data")
    String postData(@RequestBody String requestBody);
}
```

使用Feign客户端的服务类：

```java
import org.springframework.stereotype.Service;

@Service
public class FeignHttpService {

    private final ExampleApiClient exampleApiClient;

    public FeignHttpService(ExampleApiClient exampleApiClient) {
        this.exampleApiClient = exampleApiClient;
    }

    public String getExample() {
        return exampleApiClient.getData();
    }

    public String postExample() {
        String requestBody = "{\"key\":\"value\"}";
        return exampleApiClient.postData(requestBody);
    }
}
```

### 4. HttpClient

Apache HttpClient是一个功能丰富的HTTP客户端库，可以与Spring Boot集成使用。

#### 特点：

- 功能丰富
- 高度可定制
- 支持复杂的HTTP操作
- 良好的连接管理


#### 示例代码：

首先，添加HttpClient依赖到你的pom.xml：

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>
```

配置HttpClient：

```java
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }
}
```

使用HttpClient的服务类：

```java
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HttpClientService {

    private final CloseableHttpClient httpClient;

    public HttpClientService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getExample() throws IOException {
        HttpGet request = new HttpGet("https://api.example.com/data");
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        }
    }

    public String postExample() throws IOException {
        HttpPost request = new HttpPost("https://api.example.com/data");
        String json = "{\"key\":\"value\"}";
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        }
    }
}
```

这些是Spring Boot中发送HTTP请求的主要方式。每种方法都有其优点和适用场景：

- RestTemplate适合简单的同步请求。
- WebClient适合需要非阻塞或响应式编程的场景。
- OpenFeign适合需要声明式API的微服务架构。
- HttpClient适合需要更多控制和自定义的复杂HTTP操作。

选择哪种方法取决于你的具体需求、性能要求和项目架构。在现代Spring应用中，WebClient正在逐渐取代RestTemplate成为推荐的HTTP客户端。

### 5.RestTemplate详情



`RestTemplate` 是 Spring 提供的一个同步 HTTP 请求客户端，用于向 RESTful 服务发起请求。它提供了多种方法来发送不同类型的 HTTP 请求，并且可以处理不同类型的响应。

### 常用方法及其区别

1. **`getForObject()`**

   - **功能**: 发起一个 HTTP GET 请求，并将响应体转换为指定类型的对象。

   - **用法**: 当你只关心响应的内容，并且希望直接将其转换为 Java 对象时使用。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource";
     MyClass response = restTemplate.getForObject(url, MyClass.class);
     ```

2. **`getForEntity()`**

   - **功能**: 发起一个 HTTP GET 请求，并返回一个 `ResponseEntity` 对象，该对象包含了响应体、响应头、状态码等信息。

   - **用法**: 当你不仅关心响应体，还希望获取更多的响应信息（如状态码、响应头等）时使用。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource";
     ResponseEntity<MyClass> responseEntity = restTemplate.getForEntity(url, MyClass.class);
     MyClass responseBody = responseEntity.getBody();
     HttpStatus statusCode = responseEntity.getStatusCode();
     ```

3. **`postForObject()`**

   - **功能**: 发起一个 HTTP POST 请求，发送请求体并返回响应体（转换为指定类型的对象）。

   - **用法**: 当你需要发送数据到服务器并希望获得响应体的内容时使用。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource";
     MyClass request = new MyClass();
     MyClass response = restTemplate.postForObject(url, request, MyClass.class);
     ```

4. **`postForEntity()`**

   - **功能**: 发起一个 HTTP POST 请求，发送请求体，并返回一个 `ResponseEntity` 对象，包含响应体、响应头、状态码等信息。

   - **用法**: 如果需要获取更多响应信息（如状态码、响应头等），而不仅仅是响应体时使用。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource";
     MyClass request = new MyClass();
     ResponseEntity<MyClass> responseEntity = restTemplate.postForEntity(url, request, MyClass.class);
     MyClass responseBody = responseEntity.getBody();
     HttpStatus statusCode = responseEntity.getStatusCode();
     ```

5. **`put()`**

   - **功能**: 发起一个 HTTP PUT 请求，用于更新资源。

   - **用法**: 当你需要更新服务器上的资源时使用。通常不返回任何内容，只返回状态码。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource";
     MyClass request = new MyClass();
     restTemplate.put(url, request);
     ```

6. **`delete()`**

   - **功能**: 发起一个 HTTP DELETE 请求，用于删除资源。

   - **用法**: 当你需要删除服务器上的资源时使用。通常不返回任何内容，只返回状态码。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource/{id}";
     restTemplate.delete(url, 1);  // 1是资源的ID
     ```

7. **`exchange()`**

   - **功能**: 提供更加通用的方法，可以发起任何类型的 HTTP 请求（GET、POST、PUT、DELETE等），并且可以访问完整的响应信息（例如响应头、状态码、响应体等）。

   - **用法**: 用于更复杂的请求场景，或者需要处理不同类型的 HTTP 方法（GET、POST、PUT、DELETE等）时使用。

   - 示例

     :

     ```java
     RestTemplate restTemplate = new RestTemplate();
     String url = "https://api.example.com/resource";
     HttpHeaders headers = new HttpHeaders();
     headers.set("Authorization", "Bearer token");
     HttpEntity<MyClass> entity = new HttpEntity<>(request, headers);
     ResponseEntity<MyClass> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, MyClass.class);
     ```

### 方法区别总结

| 方法              | 功能                                   | 返回类型       | 适用场景                                 |
| ----------------- | -------------------------------------- | -------------- | ---------------------------------------- |
| `getForObject()`  | 发起 GET 请求并返回响应体对象          | T (响应体)     | 只关心响应体内容，且不需要状态码和响应头 |
| `getForEntity()`  | 发起 GET 请求并返回响应体 + 状态码/头  | ResponseEntity | 需要获取状态码和响应头信息               |
| `postForObject()` | 发起 POST 请求并返回响应体对象         | T (响应体)     | 发送数据并获取响应体                     |
| `postForEntity()` | 发起 POST 请求并返回响应体 + 状态码/头 | ResponseEntity | 发送数据并获取响应体、状态码和响应头     |
| `put()`           | 发起 PUT 请求进行资源更新              | void           | 更新资源，通常无响应体                   |
| `delete()`        | 发起 DELETE 请求删除资源               | void           | 删除资源，通常无响应体                   |
| `exchange()`      | 发起任意 HTTP 方法的请求，获取完整响应 | ResponseEntity | 需要更复杂的请求或自定义请求头时使用     |

### 总结

- **`getForObject()`** 和 **`postForObject()`** 适合你只关心响应体的简单场景。
- **`getForEntity()`** 和 **`postForEntity()`** 适合你还需要响应的状态码和响应头时使用。
- **`exchange()`** 提供了更多的灵活性和控制，适用于复杂的 HTTP 请求场景。
- **`put()`** 和 **`delete()`** 分别用于更新和删除资源，通常没有返回值。