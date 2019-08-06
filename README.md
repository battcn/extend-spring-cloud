## 为什么有这个包

- **目前开源项目中的工具包 `starter` 包层出不穷，为什么不借鉴 Spring Boot 统一管理起来呢，抛开`dubbo`、`nacos`、`sharding-sphere` 这种大型开源项目自己的 `Starter` 包，那么一些小的好用的工具包又能进行复用，何不开源共享**
- **QQ交流：1837307557**

**使用方式：在您的 `POM.XML` 中添加如下内容**

## 已实现模块

- **Hystrix： 解决 `Hystrix` 在Thread策略下 `ThreadLocal` 传递请求头的问题**
- **Gateway： 增加了黑白名单过滤器**



```xml
<dependency>
    <groupId>com.battcn.cloud</groupId>
    <artifactId>extend-具体的模块-spring-cloud-starter</artifactId>
    <version>${extend-spring-boot.version}</version>
</dependency>
```

**动态控制：可以通过 `enabled` 动态控制，如果依赖了模块，默认开启使用**
``` properties
extend.模块.enabled=false
```