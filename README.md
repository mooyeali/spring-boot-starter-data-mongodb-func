# Spring Boot Starter for Functional MongoDB CRUD

## 项目简介

本项目是一个基于 Spring Boot 2.x 的 Starter，用于简化 MongoDB 的功能式 CRUD 操作。通过 Lambda 表达式和函数式编程风格，提供更灵活的数据库操作方式。

## 功能特性

- 支持基于 Lambda 表达式的查询和更新操作。
- 简化 MongoDB 的 CRUD 操作，减少样板代码。
- 与 Spring Boot 2.x 兼容。

## 快速开始

### 依赖配置

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>cn.com.mooyea</groupId>
    <artifactId>spring-boot-starter-data-mongodb-func</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 使用示例

#### 查询示例

```java
// 使用 LambdaQuery 进行查询
List<User> users = lambdaQuery(User.class)
    .where(User::getName).eq("John")
    .list();
```

#### 更新示例

```java
// 使用 LambdaUpdate 进行更新
lambdaUpdate(User.class)
    .where(User::getName).eq("John")
    .set(User::getAge, 30)
    .update();
```

## 构建与运行

### 构建项目

```bash
mvn clean install
```

### 运行测试

```bash
mvn test
```

## 贡献

欢迎提交 Issue 或 Pull Request 来改进本项目。

## 许可证

本项目采用 [MIT License](LICENSE)。