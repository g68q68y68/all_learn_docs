# Elasticsearch的restful操作



Elasticsearch 是一个分布式的搜索与分析引擎，常与 Kibana 搭配使用以提供强大的数据可视化和管理功能。通过 Kibana 的 **Dev Tools**（开发者工具），用户可以直接执行 Elasticsearch 的 RESTful API 来进行索引（Index）的增删改查、映射（Mapping）的管理以及文档（Document）的增删改查操作。本文将全面总结这些操作，重点讲述“查”的部分内容。

## 目录

1. [前置知识](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#前置知识)
2. [Kibana 中的 Dev Tools](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#kibana-中的-dev-tools)
3. 索引的增删改查
   - [创建索引](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#创建索引)
   - [读取索引信息](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#读取索引信息)
   - [更新索引设置](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#更新索引设置)
   - [删除索引](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#删除索引)
4. 结构映射（Mapping）管理
   - [创建和定义映射](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#创建和定义映射)
   - [读取映射信息](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#读取映射信息)
   - [更新映射](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#更新映射)
5. 文档的增删改查
   - [创建文档](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#创建文档)
   - [读取文档](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#读取文档)
   - [更新文档](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#更新文档)
   - [删除文档](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#删除文档)
6. 重点：读取操作详解
   - [读取索引信息](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#读取索引信息)
   - [读取映射信息](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#读取映射信息)
   - 文档查询
     - [Match 查询](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#match-查询)
     - [Term 查询](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#term-查询)
     - [范围查询（Range Query）](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#范围查询range-query)
     - [布尔查询（Bool Query）](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#布尔查询bool-query)
     - [分页与排序](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#分页与排序)
     - [聚合（Aggregations）](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#聚合-aggregations)
     - [全文搜索与高亮](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#全文搜索与高亮)
7. [示例代码](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#示例代码)
8. [总结](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#总结)

------

## 前置知识

- **Elasticsearch**：基于 Lucene 的开源搜索与分析引擎，适用于大规模数据的实时搜索与分析。
- **Kibana**：Elasticsearch 的可视化工具，提供数据可视化、仪表盘创建以及通过 Dev Tools 执行 RESTful API 的功能。
- **RESTful API**：基于 HTTP 协议的 API，使用标准的 HTTP 方法（GET、POST、PUT、DELETE 等）进行操作。

## Kibana 中的 Dev Tools

在 Kibana 中，**Dev Tools** 提供了一个控制台界面，允许用户直接编写并执行 Elasticsearch 的 RESTful 请求。默认情况下，控制台使用类似 cURL 的语法，并提供语法高亮和自动完成功能，极大地简化了与 Elasticsearch 的交互。

## 索引的增删改查

### 创建索引

索引是 Elasticsearch 中存储和管理文档的基本单位。创建索引时，可以定义其设置和映射。

**示例：创建名为 `books` 的索引，并定义基本设置**

```http
PUT /books
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 2
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "author": { "type": "keyword" },
      "published_date": { "type": "date" },
      "content": { "type": "text" }
    }
  }
}
```

### 读取索引信息

获取指定索引的详细信息，包括设置和映射。

**示例：获取 `books` 索引的信息**

```http
GET /books
```

或更详细地获取设置和映射：

```http
GET /books?pretty
```

### 更新索引设置

某些索引设置可以在索引创建后进行更新，如副本数量。

**示例：将 `books` 索引的副本数量更新为 3**

```http
PUT /books/_settings
{
  "number_of_replicas": 3
}
```

> **注意**：并非所有设置都可以动态更新，某些需要重新创建索引。

### 删除索引

移除指定的索引及其所有文档。

**示例：删除 `books` 索引**

```http
DELETE /books
```

------

## 结构映射（Mapping）管理

映射定义了索引中文档的结构和字段类型。合理的映射有助于优化搜索和分析性能。

### 创建和定义映射

在创建索引时，可以同时定义映射；也可以在索引创建后通过 API 添加映射。

**示例：在创建索引时定义映射**

```http
PUT /library
{
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "author": { "type": "keyword" },
      "published_date": { "type": "date" },
      "content": { "type": "text" }
    }
  }
}
```

### 读取映射信息

获取指定索引的映射详情。

**示例：获取 `library` 索引的映射**

```http
GET /library/_mapping
```

### 更新映射

向现有映射添加新的字段。注意，某些字段类型不能更改或删除，需谨慎操作。

**示例：向 `library` 索引添加 `genre` 字段**

```http
PUT /library/_mapping
{
  "properties": {
    "genre": { "type": "keyword" }
  }
}
```

------

## 文档的增删改查

文档是索引中的基本数据单元，类似于数据库中的记录。

### 创建文档

向索引中添加新文档，可以指定文档 ID 或让 Elasticsearch 自动生成。

**示例：向 `books` 索引添加一个文档，指定 ID 为 1**

```http
POST /books/_doc/1
{
  "title": "Elasticsearch 入门",
  "author": "张三",
  "published_date": "2023-01-15",
  "content": "这是一本关于 Elasticsearch 的入门书籍。"
}
```

**示例：让 Elasticsearch 自动生成文档 ID**

```http
POST /books/_doc
{
  "title": "Kibana 实战",
  "author": "李四",
  "published_date": "2023-05-20",
  "content": "深入浅出地介绍了 Kibana 的各种功能。"
}
```

### 读取文档

获取指定 ID 的文档内容。

**示例：获取 `books` 索引中 ID 为 1 的文档**

```http
GET /books/_doc/1
```

### 更新文档

修改已存在的文档内容，可以部分更新。

**示例：更新 `books` 索引中 ID 为 1 的文档的作者**

```http
POST /books/_update/1
{
  "doc": {
    "author": "王五"
  }
}
```

### 删除文档

移除指定 ID 的文档。

**示例：删除 `books` 索引中 ID 为 2 的文档**

```http
DELETE /books/_doc/2
```

------

## 重点：读取操作详解

读取操作在 Elasticsearch 中极为重要，涵盖了索引信息、映射信息以及文档的查询。以下将详细介绍这些“查”操作的具体方法和示例。

### 读取索引信息

获取索引的基本信息，如设置、健康状态、文档数量等。

**示例：获取 `books` 索引的基本信息**

```http
GET /books/_stats
```

**示例：获取所有索引的健康状态**

```http
GET /_cat/indices?v
```

### 读取映射信息

查看索引中字段的定义和类型，有助于理解数据结构。

**示例：获取 `books` 索引的映射信息**

```http
GET /books/_mapping
```

### 文档查询

Elasticsearch 提供了丰富的查询 DSL（Domain Specific Language），支持各种复杂的查询需求。以下是几种常用的查询类型。

#### Match 查询

用于全文搜索，分析输入文本后匹配文档。

**示例：查找内容中包含“入门”的文档**

```http
GET /books/_search
{
  "query": {
    "match": {
      "content": "入门"
    }
  }
}
```

#### Term 查询

用于精确匹配，适合用于关键字、ID 等非分析字段。

**示例：查找作者为“张三”的文档**

```http
GET /books/_search
{
  "query": {
    "term": {
      "author": "张三"
    }
  }
}
```

> **注意**：`.keyword` 字段通常用于不进行分析的原始数据。如果映射中 `author` 字段为 `keyword` 类型，可以直接使用 `author`；否则，可能需要使用 `author.keyword`。

#### 范围查询（Range Query）

用于查找字段值在指定范围内的文档，常用于日期、数值等范围过滤。

**示例：查找发布日期在 2023 年之后的文档**

```http
GET /books/_search
{
  "query": {
    "range": {
      "published_date": {
        "gte": "2023-01-01"
      }
    }
  }
}
```

**示例：查找发布日期在 2023 年 1 月至 2023 年 12 月之间的文档**

```http
GET /books/_search
{
  "query": {
    "range": {
      "published_date": {
        "gte": "2023-01-01",
        "lte": "2023-12-31"
      }
    }
  }
}
```

#### 布尔查询（Bool Query）

结合多个查询条件，支持 `must`（必须满足）、`should`（应该满足）、`must_not`（必须不满足）等逻辑。

**示例：查找作者为“张三”且内容包含“入门”的文档**

```http
GET /books/_search
{
  "query": {
    "bool": {
      "must": [
        { "term": { "author": "张三" } },
        { "match": { "content": "入门" } }
      ]
    }
  }
}
```

**示例：查找内容包含“入门”或“实战”的文档**

```http
GET /books/_search
{
  "query": {
    "bool": {
      "should": [
        { "match": { "content": "入门" } },
        { "match": { "content": "实战" } }
      ],
      "minimum_should_match": 1
    }
  }
}
```

#### 分页与排序

控制查询结果的数量和顺序，适用于展示数据列表。

**示例：获取第 2 页，每页 10 条记录，按发布日期降序**

```http
GET /books/_search
{
  "from": 10,
  "size": 10,
  "sort": [
    { "published_date": { "order": "desc" } }
  ],
  "query": {
    "match_all": {}
  }
}
```

#### 聚合（Aggregations）

用于统计分析，如计数、求和、平均值、分组等，适合生成报表和可视化数据。

**示例：按作者分组并统计每个作者的文档数量**

```http
GET /books/_search
{
  "size": 0,
  "aggs": {
    "authors": {
      "terms": {
        "field": "author"
      }
    }
  }
}
```

**示例：计算所有文档的平均发布日期**

```http
GET /books/_search
{
  "size": 0,
  "aggs": {
    "average_published_date": {
      "avg": {
        "field": "published_date"
      }
    }
  }
}
```

#### 全文搜索与高亮

在搜索结果中高亮显示匹配的关键词，便于阅读和理解。

**示例：在内容中搜索“Elasticsearch”并高亮显示**

```http
GET /books/_search
{
  "query": {
    "match": {
      "content": "Elasticsearch"
    }
  },
  "highlight": {
    "fields": {
      "content": {}
    }
  }
}
```

------

## 示例代码

以下是在 Kibana Dev Tools 中执行的完整示例，展示了索引、映射及文档的增删改查操作，尤其是读取操作的不同用法。

```http
# 1. 创建索引并定义映射
PUT /library
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "author": { "type": "keyword" },
      "published_date": { "type": "date" },
      "content": { "type": "text" },
      "genre": { "type": "keyword" }
    }
  }
}

# 2. 添加文档
POST /library/_doc/1
{
  "title": "Elasticsearch 入门",
  "author": "张三",
  "published_date": "2023-01-15",
  "content": "这是一本关于 Elasticsearch 的入门书籍。",
  "genre": "技术"
}

POST /library/_doc/2
{
  "title": "Kibana 实战",
  "author": "李四",
  "published_date": "2023-05-20",
  "content": "深入浅出地介绍了 Kibana 的各种功能。",
  "genre": "技术"
}

POST /library/_doc/3
{
  "title": "数据科学基础",
  "author": "王五",
  "published_date": "2022-11-10",
  "content": "介绍数据科学的基本概念和方法。",
  "genre": "教育"
}

# 3. 读取操作示例

## 3.1 读取索引信息
GET /library/_stats

## 3.2 读取映射信息
GET /library/_mapping

## 3.3 Match 查询
GET /library/_search
{
  "query": {
    "match": {
      "content": "入门"
    }
  }
}

## 3.4 Term 查询
GET /library/_search
{
  "query": {
    "term": {
      "author": "张三"
    }
  }
}

## 3.5 范围查询
GET /library/_search
{
  "query": {
    "range": {
      "published_date": {
        "gte": "2023-01-01",
        "lte": "2023-12-31"
      }
    }
  }
}

## 3.6 布尔查询
GET /library/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "content": "Kibana" } },
        { "range": { "published_date": { "gte": "2023-01-01" } } }
      ]
    }
  }
}

## 3.7 聚合查询
GET /library/_search
{
  "size": 0,
  "aggs": {
    "genres": {
      "terms": {
        "field": "genre"
      }
    }
  }
}

## 3.8 高亮查询
GET /library/_search
{
  "query": {
    "match": {
      "content": "Elasticsearch"
    }
  },
  "highlight": {
    "fields": {
      "content": {}
    }
  }
}

# 4. 更新操作
POST /library/_update/1
{
  "doc": {
    "genre": "计算机科学"
  }
}

# 5. 删除操作
DELETE /library/_doc/2
```

------

## 总结

通过 Kibana 的 Dev Tools，用户可以方便地执行 Elasticsearch 的 RESTful API，实现对索引、映射及文档的增删改查操作。尤其是在读取操作方面，Elasticsearch 提供了丰富的查询类型和功能，满足各种复杂的数据检索需求。掌握这些基本操作和查询技巧，能够帮助用户更高效地管理和分析数据，充分发挥 Elasticsearch 的强大性能。

### 关键要点

- **索引管理**：包括创建、读取、更新和删除索引，合理配置索引设置（如分片和副本）以优化性能。
- **映射管理**：定义和管理索引中文档的结构和字段类型，确保数据的准确性和查询效率。
- **文档操作**：增删改查文档是数据管理的核心，理解各种查询方式有助于高效检索所需信息。
- **高级查询**：掌握布尔查询、聚合、分页与排序、全文搜索与高亮等高级功能，满足复杂的数据分析需求。

### 推荐资源

- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Kibana 官方文档](https://www.elastic.co/guide/en/kibana/current/index.html)
- [Elasticsearch 查询 DSL](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl.html)

通过系统地学习和实践上述内容，您将能够熟练掌握 Elasticsearch 与 Kibana 的结合使用，构建高效的数据搜索与分析平台。

#  

 **Spring Data Elasticsearch**、**Elasticsearch REST Client** 以及 **第三方库（如 Jest）**。

## 目录

1. [前置知识](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#前置知识)

2. 整合方式概述

   - [使用 Spring Data Elasticsearch](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#使用-spring-data-elasticsearch)
   - [使用 Elasticsearch REST Client](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#使用-elasticsearch-rest-client)
   - [使用第三方库 Jest](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#使用第三方库-jest)

3. 详细操作步骤

   - 1. 使用 Spring Data Elasticsearch

     - [添加依赖](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#添加依赖)
     - [配置 Elasticsearch 连接](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#配置-elasticsearch-连接)
     - [定义实体类](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#定义实体类)
     - [创建 Repository 接口](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#创建-repository-接口)
     - [基本 CRUD 操作](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#基本-crud-操作)
     - [自定义查询](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#自定义查询)

   - 2. 使用 Elasticsearch REST Client

     - [添加依赖](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#添加依赖-1)
     - [配置 Elasticsearch 连接](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#配置-elasticsearch-连接-1)
     - [使用 RestHighLevelClient 进行操作](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#使用-resthighlevelclient-进行操作)
     - [示例代码](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#示例代码)

   - 3. 使用 Jest

     - [添加依赖](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#添加依赖-2)
     - [配置 Jest 客户端](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#配置-jest-客户端)
     - [执行操作](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#执行操作)

4. [注意事项](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#注意事项)

5. [总结](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#总结)

6. [参考资料](https://www.chatgpt-open.com/c/677de3e3-af04-8004-9ad5-9ad92b62c897#参考资料)

------

## 前置知识

在开始整合之前，确保您具备以下基础知识：

- **Spring Boot**：熟悉 Spring Boot 的基本概念和项目结构。
- **Elasticsearch**：了解 Elasticsearch 的基本概念，如索引、文档、映射等。
- **Maven 或 Gradle**：了解如何在 Spring Boot 项目中管理依赖。

## 整合方式概述

整合 Spring Boot 与 Elasticsearch 主要有以下几种方式：

1. **使用 Spring Data Elasticsearch**：这是最常用的方法，Spring Data 提供了对 Elasticsearch 的全面支持，简化了 CRUD 操作和复杂查询的实现。
2. **使用 Elasticsearch REST Client**：适合需要更底层控制的场景，直接使用官方提供的 REST 客户端进行操作。
3. **使用第三方库 Jest**：Jest 是一个基于 REST 的 Java 客户端，提供了一些额外的功能和简化的 API。

下面将详细介绍每种整合方式的操作步骤和示例代码。

------

## 1. 使用 Spring Data Elasticsearch

### 添加依赖

首先，在您的 `pom.xml`（Maven）或 `build.gradle`（Gradle）中添加 Spring Data Elasticsearch 的依赖。

**Maven 示例：**

```xml
<dependencies>
    <!-- Spring Data Elasticsearch -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
    </dependency>
    <!-- 其他依赖 -->
</dependencies>
```

**Gradle 示例：**

```groovy
dependencies {
    // Spring Data Elasticsearch
    implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'
    // 其他依赖
}
```

### 配置 Elasticsearch 连接

在 `application.properties` 或 `application.yml` 文件中配置 Elasticsearch 的连接信息。

**application.properties 示例：**

```properties
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.username=elastic
spring.elasticsearch.password=your_password
spring.elasticsearch.rest.read-timeout=5s
```

**application.yml 示例：**

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: your_password
    rest:
      read-timeout: 5s
```

**注意**：根据您的 Elasticsearch 配置调整 `uris`、`username` 和 `password`。

### 定义实体类

创建一个与 Elasticsearch 索引对应的实体类，并使用注解进行映射。

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "books")
public class Book {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Date)
    private String publishedDate;

    @Field(type = FieldType.Text)
    private String content;

    // 构造方法、Getters 和 Setters
}
```

### 创建 Repository 接口

创建一个继承自 `ElasticsearchRepository` 的接口，以便进行 CRUD 操作和自定义查询。

```java
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface BookRepository extends ElasticsearchRepository<Book, String> {
    
    // 根据作者查找
    List<Book> findByAuthor(String author);
    
    // 根据标题包含关键词查找
    List<Book> findByTitleContaining(String keyword);
    
    // 自定义查询示例
    List<Book> findByPublishedDateBetween(String startDate, String endDate);
}
```

### 基本 CRUD 操作

通过 `BookRepository` 接口，可以轻松实现基本的增删改查操作。

**添加或更新文档：**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
}
```

**查询文档：**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }
}
```

**删除文档：**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public void deleteBookById(String id) {
        bookRepository.deleteById(id);
    }
}
```

### 自定义查询

Spring Data Elasticsearch 支持基于方法名的查询，也可以使用 `@Query` 注解编写自定义查询。

**使用方法名查询：**

```java
List<Book> findByAuthorAndTitle(String author, String title);
```

**使用 `@Query` 注解：**

```java
import org.springframework.data.elasticsearch.annotations.Query;

public interface BookRepository extends ElasticsearchRepository<Book, String> {
    
    @Query("{\"bool\": {\"must\": [{\"match\": {\"author\": \"?0\"}}, {\"match\": {\"title\": \"?1\"}}]}}")
    List<Book> findBooksByAuthorAndTitle(String author, String title);
}
```

------

## 2. 使用 Elasticsearch REST Client

如果需要更底层的控制，或者需要使用 Spring Data Elasticsearch 不支持的功能，可以直接使用 Elasticsearch 官方提供的 REST 客户端。

### 添加依赖

在 `pom.xml` 或 `build.gradle` 中添加 Elasticsearch REST High-Level Client 的依赖。

**Maven 示例：**

```xml
<dependencies>
    <!-- Elasticsearch REST High-Level Client -->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.17.0</version> <!-- 请根据实际版本调整 -->
    </dependency>
    <!-- 其他依赖 -->
</dependencies>
```

**Gradle 示例：**

```groovy
dependencies {
    // Elasticsearch REST High-Level Client
    implementation 'org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.0'
    // 其他依赖
}
```

### 配置 Elasticsearch 连接

创建一个配置类，配置 `RestHighLevelClient` 的 Bean。

```java
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.HttpHost;

@Configuration
public class ElasticsearchConfig {
    
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("localhost", 9200, "http")
                // 如果有多个节点，可以添加更多 HttpHost
            )
        );
    }
}
```

### 使用 RestHighLevelClient 进行操作

通过注入 `RestHighLevelClient`，可以执行各种 Elasticsearch 操作。

**示例：添加文档**

```java
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Service
public class ElasticsearchService {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public String addDocument(String index, Object obj) throws IOException {
        IndexRequest request = new IndexRequest(index);
        String json = objectMapper.writeValueAsString(obj);
        request.source(json, org.elasticsearch.common.xcontent.XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getId();
    }
}
```

**示例：查询文档**

```java
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class ElasticsearchService {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public <T> T getDocument(String index, String id, Class<T> clazz) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String json = getResponse.getSourceAsString();
            return objectMapper.readValue(json, clazz);
        }
        return null;
    }
}
```

**示例：删除文档**

```java
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticsearchService {
    
    @Autowired
    private RestHighLevelClient client;
    
    public void deleteDocument(String index, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        // 可以根据需要处理 deleteResponse
    }
}
```

**示例：搜索文档**

```java
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public <T> List<T> searchDocuments(String index, String field, String value, Class<T> clazz) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery(field, value));
        searchRequest.source(sourceBuilder);
        
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<T> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            String json = hit.getSourceAsString();
            T obj = objectMapper.readValue(json, clazz);
            results.add(obj);
        }
        return results;
    }
}
```

### 示例代码

结合上述操作，以下是一个完整的示例服务类：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {
    
    @Autowired
    private RestHighLevelClient client;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public String addDocument(String index, Object obj) throws IOException {
        IndexRequest request = new IndexRequest(index);
        String json = objectMapper.writeValueAsString(obj);
        request.source(json, org.elasticsearch.common.xcontent.XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getId();
    }
    
    public <T> T getDocument(String index, String id, Class<T> clazz) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String json = getResponse.getSourceAsString();
            return objectMapper.readValue(json, clazz);
        }
        return null;
    }
    
    public void deleteDocument(String index, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
    }
    
    public <T> List<T> searchDocuments(String index, String field, String value, Class<T> clazz) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery(field, value));
        searchRequest.source(sourceBuilder);
        
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<T> results = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            String json = hit.getSourceAsString();
            T obj = objectMapper.readValue(json, clazz);
            results.add(obj);
        }
        return results;
    }
}
```

------

## 3. 使用第三方库 Jest

Jest 是一个基于 HTTP 的 Java 客户端，用于与 Elasticsearch 交互。它提供了简洁的 API 和丰富的功能，适合一些特定场景下的使用。

### 添加依赖

在 `pom.xml` 或 `build.gradle` 中添加 Jest 的依赖。

**Maven 示例：**

```xml
<dependencies>
    <!-- Jest Client -->
    <dependency>
        <groupId>io.searchbox</groupId>
        <artifactId>jest</artifactId>
        <version>6.3.1</version> <!-- 请根据实际版本调整 -->
    </dependency>
    <!-- 其他依赖 -->
</dependencies>
```

**Gradle 示例：**

```groovy
dependencies {
    // Jest Client
    implementation 'io.searchbox:jest:6.3.1'
    // 其他依赖
}
```

### 配置 Jest 客户端

创建一个配置类，配置 JestClient 的 Bean。

```java
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JestConfig {
    
    @Bean
    public JestClient jestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(
            new HttpClientConfig.Builder("http://localhost:9200")
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(2)
                .maxTotalConnection(10)
                .build()
        );
        return factory.getObject();
    }
}
```

### 执行操作

通过注入 `JestClient`，可以执行各种 Elasticsearch 操作。

**示例：添加文档**

```java
import io.searchbox.core.Index;
import io.searchbox.core.IndexResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class JestService {
    
    @Autowired
    private JestClient jestClient;
    
    public String addDocument(String index, String type, Object obj) throws IOException {
        Index indexRequest = new Index.Builder(obj)
            .index(index)
            .type(type)
            .build();
        IndexResult result = jestClient.execute(indexRequest);
        return result.getId();
    }
}
```

**示例：查询文档**

```java
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class JestService {
    
    @Autowired
    private JestClient jestClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public <T> List<T> searchDocuments(String index, String query, Class<T> clazz) throws IOException {
        String jsonQuery = "{ \"query\": { \"match\": { \"content\": \"" + query + "\" } } }";
        Search search = new Search.Builder(jsonQuery)
            .addIndex(index)
            .build();
        SearchResult result = jestClient.execute(search);
        return result.getSourceAsObjectList(clazz);
    }
}
```

**示例：删除文档**

```java
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class JestService {
    
    @Autowired
    private JestClient jestClient;
    
    public void deleteDocument(String index, String type, String id) throws IOException {
        Delete delete = new Delete.Builder(id)
            .index(index)
            .type(type)
            .build();
        DeleteResult result = jestClient.execute(delete);
    }
}
```

------

## 注意事项

1. **版本兼容性**：确保 Spring Boot、Spring Data Elasticsearch 和 Elasticsearch 服务器版本的兼容性。不同版本之间可能存在 API 或协议的不兼容问题。建议参考 [Elasticsearch 官方文档](https://www.elastic.co/guide/index.html) 和 [Spring Data Elasticsearch 官方文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/) 以确认版本匹配。
2. **配置安全性**：在生产环境中，Elasticsearch 通常需要身份验证和安全配置。确保在配置连接时正确设置 `username`、`password` 以及使用 HTTPS 协议（如果启用）。
3. **索引和映射管理**：合理设计索引和映射，可以显著提升查询性能和数据存储效率。使用 Spring Data Elasticsearch 时，可以通过注解定义映射，也可以在 Elasticsearch 中手动管理。
4. **资源管理**：尤其在使用 `RestHighLevelClient` 或 `JestClient` 时，确保正确关闭客户端连接，避免资源泄漏。可以通过配置 Bean 的销毁方法或使用 Spring 的生命周期管理功能来实现。
5. **性能优化**：对于大规模数据操作，考虑批量操作（Bulk API）、分页查询、缓存策略等优化手段，以提升应用性能和用户体验。

------

## 总结

将 Spring Boot 与 Elasticsearch 整合，可以充分发挥两者的优势，构建高效、可扩展的数据搜索与分析应用。主要有以下几种整合方式：

1. **Spring Data Elasticsearch**：适合大多数常见场景，提供了简洁的 API 和自动化的 CRUD 操作，支持基于方法名的查询和自定义查询。
2. **Elasticsearch REST Client**：适用于需要更细粒度控制和高级功能的场景，直接与 Elasticsearch 交互，灵活性更高。
3. **第三方库 Jest**：提供了基于 HTTP 的客户端，适合特定需求下的使用，API 简洁但功能略有不同。

在选择整合方式时，应根据项目需求、团队熟悉程度和具体功能要求进行权衡。同时，注意版本兼容性和安全配置，确保应用的稳定性和安全性。

通过本文的指导，您应能熟练地将 Elasticsearch 集成到 Spring Boot 项目中，并实现基本的增删改查操作以及复杂的查询需求。结合实际项目需求，进一步探索 Elasticsearch 的高级功能，如聚合分析、全文检索、高亮显示等，将帮助您构建更加强大和高效的数据处理应用。

------

## 参考资料

- [Spring Data Elasticsearch 官方文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Elasticsearch 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Elasticsearch REST High-Level Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
- [Jest 官方 GitHub 仓库](https://github.com/searchbox-io/Jest)