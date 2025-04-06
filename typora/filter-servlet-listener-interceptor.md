# filter-servlet-listener-interceptor



Spring Boot中创建过滤器(Filter)、Servlet、监听器(Listener)和拦截器(Interceptor)的方式。这些组件都是Web应用程序中非常重要的部分,用于处理请求、响应和应用程序事件。

让我们逐一讨论这些组件:

### 1. 过滤器 (Filter)

过滤器用于对请求和响应进行预处理和后处理。

#### 创建方式:

1. 实现 `javax.servlet.Filter` 接口
2. 使用 `@Component` 或 `@WebFilter` 注解或者`FilterRegistrationBean()方法`


#### 示例代码:

```java
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CustomFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化逻辑
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        System.out.println("CustomFilter processing request: " + httpRequest.getRequestURI());
        
        // 在请求处理之前的逻辑
        chain.doFilter(request, response);
        // 在响应处理之后的逻辑
    }

    @Override
    public void destroy() {
        // 销毁逻辑
    }
}
```

### 2. Servlet

Servlet是处理Web请求的Java类。

#### 创建方式:

1. 继承 `javax.servlet.http.HttpServlet` 类
2. 使用 `@WebServlet` 注解


#### 示例代码:

```java
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<CustomServlet> customServletBean() {
        ServletRegistrationBean<CustomServlet> bean = new ServletRegistrationBean<>(new CustomServlet(), "/custom/*");
        bean.setLoadOnStartup(1);
        return bean;
    }  
  @Bean
    public FilterRegistrationBean<CustomServlet> customFilterBean() {
        ServletRegistrationBean<CustomServlet> bean = new ServletRegistrationBean<>(new CustomServlet(), "/custom/*");
        bean.setLoadOnStartup(1);
        return bean;
    }  

}

class CustomServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Hello from CustomServlet");
    }
}
```

### 3. 监听器 (Listener)

监听器用于监听Web应用程序中的各种事件。

#### 创建方式:

1. 实现相应的监听器接口(如 `ServletContextListener`, `HttpSessionListener` 等)
2. 使用 `@WebListener` 注解


#### 示例代码:

```java
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@Component
public class CustomContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContext initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ServletContext destroyed");
    }
}
```

### 4. 拦截器 (Interceptor)

拦截器用于在控制器处理请求的前后执行自定义逻辑。

#### 创建方式:

1. 实现 `org.springframework.web.servlet.HandlerInterceptor` 接口
2. 在 WebMvcConfigurer 中注册拦截器


#### 示例代码:

```java
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("CustomInterceptor: preHandle");
        return true; // 返回 true 继续执行，返回 false 中断请求
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("CustomInterceptor: postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("CustomInterceptor: afterCompletion");
    }
}
```

注册拦截器:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomInterceptor customInterceptor;

    public WebConfig(CustomInterceptor customInterceptor) {
        this.customInterceptor = customInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customInterceptor).addPathPatterns("/**");
    }
}
```

### 总结

1. **过滤器 (Filter)**:

1. 实现 `javax.servlet.Filter` 接口
2. 使用 `@Component` 或 `@WebFilter` 注解
3. 用于请求和响应的预处理和后处理



2. **Servlet**:

1. 继承 `javax.servlet.http.HttpServlet` 类
2. 使用 `@WebServlet` 注解或通过 `ServletRegistrationBean` 注册
3. 处理特定的 URL 模式的请求



3. **监听器 (Listener)**:

1. 实现相应的监听器接口
2. 使用 `@WebListener` 注解
3. 监听 Web 应用程序中的各种事件



4. **拦截器 (Interceptor)**:

1. 实现 `org.springframework.web.servlet.HandlerInterceptor` 接口
2. 在 `WebMvcConfigurer` 中注册
3. 在控制器处理请求的前后执行自定义逻辑





这些组件在 Spring Boot 应用程序中扮演着不同的角色,可以根据具体需求选择合适的组件来实现所需的功能。过滤器和监听器是 Servlet 规范的一部分,而拦截器是 Spring MVC 的一部分。通常,过滤器用于更广泛的请求处理(如编码转换、跨域请求处理等),而拦截器更多用于与应用程序业务逻辑相关的处理。