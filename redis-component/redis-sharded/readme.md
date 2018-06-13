# ShardedJedis 操作 redis

## 集成方式

通过 JavaConfig 配置 ShardedJedis 相关 bean 

## 使用方式

1. 引入包
2. 在配置文件中  import application-sharded-redis.xml
3. 注入 ShardedJedisPool
 
## 测试方式

为了快速测试, 使用 Spring Boot 做集成测试

只有一个测试功能, 因为不同的 Jedis 连接模式配置文件不一样, 因此使用最简单的方式, 即根据不同的环境加载不同的配置文件

因此需要设置测试类的 Environment variables

此模式需要添加 

spring.profiles.acrive=sharded

**配置文件为**

`application-sharded.properties`

## 注意

这里使用 redis 标准 uri 来进行连接配置

redis uri 格式如下:

需要密码的格式

```
redis://password@ip:port/database
```

不需要密码的格式

```
redis://ip:port/database
```
