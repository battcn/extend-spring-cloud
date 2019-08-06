## 为什么有这个包

- **目前开源项目中的工具包 `starter` 包层出不穷，为什么不借鉴 Spring Boot 统一管理起来呢，抛开`dubbo`、`nacos`、`sharding-sphere` 这种大型开源项目自己的 `Starter` 包，那么一些小的好用的工具包又能进行复用，何不开源共享**
- **QQ交流：1837307557**

**使用方式：在您的 `POM.XML` 中添加如下内容**

## 已实现模块

- **Hystrix： 解决 `Hystrix` 在Thread策略下 `ThreadLocal` 传递请求头的问题（代码来自周立大神提交到Spring Cloud ISSUE）由于该功能有不少应用场景，官方未接收 PULL 所以我将它移植过来稍作修改打成了 starter 包**
- **Gateway： 增加了黑白名单过滤器**

## 用法

```xml
<dependency>
    <groupId>com.battcn.cloud</groupId>
    <artifactId>extend-具体的模块-spring-cloud-starter</artifactId>
    <version>${extend-spring-cloud.version}</version>
</dependency>
```

**动态控制：可以通过 `enabled` 动态控制，如果依赖了模块，默认开启使用**

``` properties
extend.模块.enabled=false
```


已添加了演示案例，用的 `nacos` 如果本地没有那么运行不起来，当然可以直接依赖对应的包 copy 配置即可

### Hystrix 包

**可以参考`extend-hystrix-spring-cloud-sample`演示案例的配置内容**

``` yaml
# 开启/关闭配置
extend:
  hystrix:
    propagate:
      request-attribute:
        enabled: true
hystrix:
  command:
    default:
      circuitBreaker:
        sleepWindowInMilliseconds: 100000
        forceClosed: true
      execution:
        isolation:
          # 如果策略是  THREAD   那么 开启了上面的配置 feign 就无感知传递 header 了
          strategy: THREAD
          thread:
            timeoutInMilliseconds: 600000
  shareSecurityContext: true
```


### Gateway 包（只能在Spring-Cloud-Gateway 网关层用）

**可以参考`extend-gateway-spring-cloud-sample`演示案例的配置内容**

``` yaml
spring:
  application:
    name: extend-gateway-spring-cloud-sample
  cloud:
    nacos:
      discovery:
        server-addr: 192.168.1.13:7777
    gateway:
      discovery:
        locator:
          enabled: false
      routes:
        - id: extend-hystrix-spring-cloud-sample
          uri: lb://extend-hystrix-spring-cloud-sample
          predicates:
            - Path=/extend-hystrix/**
          filters:
            # 切割路径
            - StripPrefix=1
            # 添加黑白名单过滤
            - name: BlackWhiteList
              args:
                max-trusted-index: 1
                black-lists: 192.168.1.13,0:0:0:0:0:0:0:1,127.0.0.1
                #white-lists: 192.168.1.14
extend:
  gateway:
    filter:
      black-white-list:
        # 首先要开启黑白名单过滤器（当然默认值是 true ）
        enabled: true                
```

#### 响应结果（默认，可以自定义）

``` json
{
  "timestamp": 1565083229684,
  "message": "访问受限，请联系管理员",
  "successful": false,
  "messageId": 403
}
```