# springbootDataJPA使用

讲解Spring Boot中使用JPA(Java Persistence API)的相关内容,包括常用注解、接口和配置信息。

### 1. JPA简介

JPA是Java持久化API的标准规范,Spring Data JPA是Spring基于JPA规范的一套解决方案。在Spring Boot中使用JPA可以大大简化数据库操作。

### 2. 常用注解

#### 2.1 实体类注解

1. `@Entity`: 标记一个类为实体类
2. `@Table`: 指定实体类对应的数据库表名
3. `@Id`: 标记一个字段为主键
4. `@GeneratedValue`: 指定主键的生成策略
5. `@Column`: 指定字段对应的数据库列名及其他属性
6. `@Transient`: 标记一个字段不需要持久化
7. `@Temporal`: 指定日期类型的字段映射


示例:

```java
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Transient
    private String tempField;

    // Getters and setters
}
```

#### 2.2 关系映射注解

1. `@OneToOne`: 一对一关系
2. `@OneToMany`: 一对多关系
3. `@ManyToOne`: 多对一关系
4. `@ManyToMany`: 多对多关系
5. `@JoinColumn`: 指定外键列
6. `@JoinTable`: 指定中间表(用于多对多关系)


示例:

```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // Other fields, getters and setters
}

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Other fields, getters and setters
}
```

### 3. JPA接口

Spring Data JPA提供了几个重要的接口,可以大大简化数据库操作:

1. `JpaRepository<T, ID>`: 提供了基本的CRUD操作
2. `PagingAndSortingRepository<T, ID>`: 提供分页和排序功能
3. `CrudRepository<T, ID>`: 提供基本的CRUD操作(不包含分页和排序)


示例:

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByEmailContaining(String email);
}
```

### 4. 查询方法

Spring Data JPA支持通过方法名自动生成查询:

1. `findBy{PropertyName}`: 根据属性查找
2. `findBy{PropertyName}Containing`: 模糊查询
3. `findBy{PropertyName}Between`: 范围查询
4. `findBy{PropertyName}OrderBy{PropertyName}Desc`: 排序查询


也可以使用`@Query`注解自定义查询:

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findOrdersByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM orders WHERE total_amount > ?1", nativeQuery = true)
    List<Order> findOrdersWithTotalAmountGreaterThan(BigDecimal amount);
}
```

### 5. 常用配置信息

在`application.properties`或`application.yml`中配置JPA:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

配置说明:

- `spring.datasource`: 数据源配置
- `spring.jpa.hibernate.ddl-auto`: 数据库schema生成策略

- `update`: 更新schema
- `create`: 每次都创建新的schema
- `create-drop`: 应用停止时删除schema
- `validate`: 验证schema,不做更改



- `spring.jpa.show-sql`: 是否显示SQL语句
- `spring.jpa.properties.hibernate.dialect`: 指定数据库方言
- `spring.jpa.properties.hibernate.format_sql`: 格式化SQL输出


### 6. 事务管理

使用`@Transactional`注解来管理事务:

```java
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser(User user) {
        // 业务逻辑
        userRepository.save(user);
        // 如果这里抛出异常,事务会回滚
    }
}
```

### 7. 实际应用示例

让我们创建一个完整的示例,展示如何在Spring Boot中使用JPA:

```java
// Entity
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    // Getters and setters
}

// Repository
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPriceGreaterThan(BigDecimal price);
}

// Service
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findExpensiveProducts(BigDecimal threshold) {
        return productRepository.findByPriceGreaterThan(threshold);
    }
}

// Controller
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @GetMapping("/expensive")
    public ResponseEntity<List<Product>> getExpensiveProducts(@RequestParam BigDecimal threshold) {
        List<Product> products = productService.findExpensiveProducts(threshold);
        return ResponseEntity.ok(products);
    }
}
```

这个示例展示了如何定义实体、创建Repository、实现Service层逻辑,以及如何在Controller中使用这些组件。





通过使用Spring Data JPA,我们可以大大简化数据库操作,提高开发效率。JPA的强大功能和灵活性使得它成为Spring Boot应用程序中处理持久化层的首选方案之一。