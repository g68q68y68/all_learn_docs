# Maven的注意事项



### **1. Maven中`<type>`和`<scope>`标签的值及作用**

#### **1.1 `<type>`标签**

`<type>`用于指定 Maven 依赖的类型，默认值为 `jar`。

- 常见取值及作用

  :

  1. jar

     （默认值）:

     - 指定依赖为 JAR 包形式，这是最常见的依赖类型。

     - 例：

       ```xml
       <dependency>
           <groupId>org.apache.commons</groupId>
           <artifactId>commons-lang3</artifactId>
           <version>3.12.0</version>
           <type>jar</type>
       </dependency>
       ```

  2. pom

     :

     - 指定依赖为 POM 文件，用于导入 BOM（Bill of Materials）文件来管理统一的依赖版本。

     - 通常配合 `<scope>import` 使用。

     - 例：

       ```xml
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-dependencies</artifactId>
           <version>2.7.5</version>
           <type>pom</type>
           <scope>import</scope>
       </dependency>
       ```

  3. war

     :

     - 指定依赖为 WAR 文件，通常用于 Web 应用程序。

  4. ear

     :

     - 指定依赖为 EAR 文件，用于企业级应用程序。

  5. test-jar

     :

     - 指定依赖为测试 JAR 文件，包含测试代码。

  6. sources

     :

     - 指定依赖为源码包。

  7. javadoc

     :

     - 指定依赖为 Javadoc 文档包。

  8. zip

     、

     tar.gz

      等:

     - 可用于自定义文件类型。

------

#### **1.2 `<scope>`标签**

`<scope>`用于控制依赖的作用范围，影响依赖在不同阶段（如编译、运行、测试等）的可见性。

- 常见取值及作用

  :

  1. **compile**（默认值）:

     - 依赖在 **编译**、**测试** 和 **运行** 阶段均可用。

     - 默认值，适用于大多数库。

     - 例：

       ```xml
       <dependency>
           <groupId>org.apache.commons</groupId>
           <artifactId>commons-lang3</artifactId>
           <version>3.12.0</version>
           <scope>compile</scope>
       </dependency>
       ```

  2. **provided**:

     - 依赖在 **编译** 和 **测试** 阶段可用，但 **运行** 时需要由运行环境提供。

     - 常用于容器提供的依赖（如 Servlet API）。

     - 例：

       ```xml
       <dependency>
           <groupId>javax.servlet</groupId>
           <artifactId>javax.servlet-api</artifactId>
           <version>4.0.1</version>
           <scope>provided</scope>
       </dependency>
       ```

  3. **runtime**:

     - 依赖在 **运行** 和 **测试** 阶段可用，但 **编译** 阶段不可用。

     - 常用于 JDBC 驱动等运行时依赖。

     - 例：

       ```xml
       <dependency>
           <groupId>mysql</groupId>
           <artifactId>mysql-connector-java</artifactId>
           <version>8.0.33</version>
           <scope>runtime</scope>
       </dependency>
       ```

  4. **test**:

     - 依赖仅在 **测试编译** 和 **测试运行** 时可用。

     - 常用于测试框架（如 JUnit）。

     - 例：

       ```xml
       <dependency>
           <groupId>junit</groupId>
           <artifactId>junit</artifactId>
           <version>4.13.2</version>
           <scope>test</scope>
       </dependency>
       ```

  5. **system**:

     - 类似于 `provided`，但依赖文件需要通过绝对路径显式提供。

     - 一般不推荐使用。

     - 例：

       ```xml
       <dependency>
           <groupId>com.example</groupId>
           <artifactId>example-dependency</artifactId>
           <version>1.0</version>
           <scope>system</scope>
           <systemPath>${project.basedir}/lib/example-dependency-1.0.jar</systemPath>
       </dependency>
       ```

  6. **import**:

     - 只能用于 `<dependency>` 的 `type` 为 `pom` 时，导入一个 BOM 文件，统一管理依赖版本。

     - 例：

       ```xml
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-dependencies</artifactId>
           <version>2.7.5</version>
           <type>pom</type>
           <scope>import</scope>
       </dependency>
       ```

------

### Maven中`<optional>`标签的用法及依赖传递的控制

------

#### **1.3 `<optional>`标签的用法**

#### **作用**:

`<optional>`标签用于控制依赖的 **传递性**，使当前模块的依赖不会传递给下游模块。

- 默认情况下，Maven 的依赖是传递的，也就是说：
  - 如果 A 模块依赖 B，B 模块依赖 C，那么 A 模块也会间接依赖 C。
- 如果在 B 模块中，将 C 的依赖标记为 `<optional>true</optional>`，那么 C 模块不会传递给 A。

#### **语法**:

`<optional>`是 `<dependency>` 的子标签，用来标记依赖为可选。

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>example-library</artifactId>
    <version>1.0.0</version>
    <optional>true</optional>
</dependency>
```

#### **用法示例**:

1. **模块依赖结构**:

   - A 模块依赖 B 模块。
   - B 模块依赖 C 模块，并将其标记为可选 `<optional>true</optional>`。

   **B 模块 POM**:

   ```xml
   <dependencies>
       <dependency>
           <groupId>com.example</groupId>
           <artifactId>example-library</artifactId>
           <version>1.0.0</version>
           <optional>true</optional>
       </dependency>
   </dependencies>
   ```

2. **传递依赖结果**:

   - 如果 A 模块依赖 B 模块

     ：

     - A 模块不会自动依赖 C 模块。

   - 如果 A 模块直接依赖 C 模块

     ：

     - A 模块需要显式声明对 C 模块的依赖，才能使用 C 模块的功能。

   **A 模块 POM（需要手动添加 C）**:

   ```xml
   <dependencies>
       <dependency>
           <groupId>com.example</groupId>
           <artifactId>example-library</artifactId>
           <version>1.0.0</version>
       </dependency>
   </dependencies>
   ```

------

### **2. Maven依赖传递的控制**

Maven 的依赖传递机制默认是**递归**的，但可以通过 `<scope>` 和 `<optional>` 等标签进行控制。

#### **2.1 Maven的依赖传递机制**

1. **直接依赖**:

   - 当前模块显式声明的依赖。

   - 例：

     ```xml
     <dependency>
         <groupId>org.apache.commons</groupId>
         <artifactId>commons-lang3</artifactId>
         <version>3.12.0</version>
     </dependency>
     ```

2. **传递依赖（Transitive Dependency）**:

   - 当前模块的依赖会递归引入其依赖的依赖。

   - 例：

     ```
     A -> B -> C
     ```

3. **传递依赖的范围控制**:

   - 传递依赖会根据依赖的 

     ```
     scope
     ```

      进行限制：

     - **compile**: 默认范围，依赖在编译、测试和运行时均可用，且传递。
     - **provided**: 不会传递到下游模块。
     - **runtime**: 仅在运行时传递。
     - **test**: 不会传递到下游模块。
     - **optional**: 明确标记依赖不传递。

------

#### **2.2 依赖传递的控制方式**

Maven 提供多种方式控制依赖传递：

1. **通过 `<optional>` 禁用依赖传递**:

   - 上述 `<optional>true</optional>` 的用法是控制依赖是否传递的最常见方法。

2. **通过 `<scope>` 限制依赖范围**:

   - 使用 `scope` 标签将依赖的范围控制在编译、运行或测试等阶段。

   - 例如：

     ```xml
     <dependency>
         <groupId>javax.servlet</groupId>
         <artifactId>javax.servlet-api</artifactId>
         <version>4.0.1</version>
         <scope>provided</scope>
     </dependency>
     ```

   - **`provided` 和 `test` 的依赖不会传递到下游模块**。

3. **通过排除依赖**:

   - 如果不想让传递依赖中某些库引入当前模块，可以使用 `<exclusions>` 标签显式排除。

   - 例：

     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
         <version>2.7.5</version>
         <exclusions>
             <exclusion>
                 <groupId>org.springframework.boot</groupId>
                 <artifactId>spring-boot-starter-logging</artifactId>
             </exclusion>
         </exclusions>
     </dependency>
     ```

4. **通过依赖管理（`<dependencyManagement>`）**:

   - 父 POM 中通过 `<dependencyManagement>` 指定依赖版本，统一管理依赖。

   - 子模块需要显式声明依赖，才能使用对应版本。

   - 例：

     ```xml
     <dependencyManagement>
         <dependencies>
             <dependency>
                 <groupId>org.apache.commons</groupId>
                 <artifactId>commons-lang3</artifactId>
                 <version>3.12.0</version>
             </dependency>
         </dependencies>
     </dependencyManagement>
     ```

5. **通过 `<exclusions>` 在传递依赖中移除不需要的依赖**:

   - 在复杂的传递依赖中，显式排除不需要的依赖可以减少冲突。

------

### **3. `<optional>`与依赖范围的关系**

- `<optional>`与`scope`的配合

  :

  - `optional` 和 `scope` 是两种控制依赖传递的独立机制，可以组合使用。
  - 例如，将某依赖标记为 `optional` 和 `test`，则该依赖既不会传递到下游模块，也仅在测试时可用。



1. **`<optional>` 标签**:
   - 用于禁止依赖的传递性，避免下游模块自动继承不需要的依赖。
   - 适用于精细控制依赖关系，尤其是避免不必要的传递依赖。
2. **依赖传递的控制方法**:
   - 使用 `<scope>` 控制依赖的作用范围（如 `test`、`provided` 等）。
   - 使用 `<optional>` 禁止传递依赖。
   - 使用 `<exclusions>` 显式排除传递依赖中的特定库。
   - 使用 `<dependencyManagement>` 统一版本管理，减少冲突。



### **2. Maven中会被子项目继承的属性标签**

在 Maven 的父子项目关系中，父 POM 中定义的一些属性和配置会被子项目继承。

#### **会被子项目继承的标签**

1. **`<dependencyManagement>`**:

   - 子项目不会自动引入依赖，但会继承依赖的版本管理配置。

   - 子项目显式声明依赖时可以省略版本号。

   - 例：

     ```xml
     <dependencyManagement>
         <dependencies>
             <dependency>
                 <groupId>org.springframework</groupId>
                 <artifactId>spring-core</artifactId>
                 <version>5.3.21</version>
             </dependency>
         </dependencies>
     </dependencyManagement>
     ```

2. **`<pluginManagement>`**:

   - 子项目会继承插件的版本和配置，但不会自动启用插件。

   - 子项目显式声明插件时，会继承配置。

   - 例：

     ```xml
     <pluginManagement>
         <plugins>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-compiler-plugin</artifactId>
                 <version>3.8.1</version>
                 <configuration>
                     <source>1.8</source>
                     <target>1.8</target>
                 </configuration>
             </plugin>
         </plugins>
     </pluginManagement>
     ```

3. **`<properties>`**:

   - 子项目会继承父项目中定义的属性，可以通过 `${propertyName}` 使用。

   - 例：

     ```xml
     <properties>
         <java.version>17</java.version>
         <maven.compiler.source>${java.version}</maven.compiler.source>
     </properties>
     ```

4. **`<build>`**:

   - 子项目会继承父项目的构建配置，包括默认插件、目录结构等。

   - 例：

     ```xml
     <build>
         <plugins>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-surefire-plugin</artifactId>
                 <version>3.0.0</version>
             </plugin>
         </plugins>
     </build>
     ```

5. **`<repositories>` 和 `<pluginRepositories>`**:

   - 子项目会继承父项目定义的远程仓库和插件仓库地址。

6. **`<reporting>`**:

   - 子项目会继承报告配置。

7. **`<profiles>`**:

   - 子项目可以继承父项目中定义的 Profiles，但需要显式激活。

------

#### **不会被子项目继承的标签**

1. **`<dependencies>`**:
   - 父项目中直接声明的依赖不会被子项目自动引入。
   - 如果需要统一管理版本，应该放在 `<dependencyManagement>` 中。
2. **`<modules>`**:
   - 父项目的模块声明与子项目继承无关，主要用于多模块项目的聚合。

------

### **总结**

- **`<type>`标签**控制依赖的文件类型（如 JAR、POM、WAR），常用于自定义依赖。
- **`<scope>`标签**控制依赖的作用范围，影响依赖在不同阶段的可见性。
- Maven 的继承机制支持子项目继承父项目中的 `dependencyManagement`、`pluginManagement`、`properties`、`build` 等配置，但不会自动继承 `<dependencies>` 和 `<modules>` 标签内容。