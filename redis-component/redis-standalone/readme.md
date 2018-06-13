# Jedis 单机模式

## 集成方式

通过 JavaConfig 配置 Jedis 相关 bean 

## 使用方式

1. 引入包
2. 在配置文件中  import application-standalone-redis.xml
3. 注入 JedisPool
 
## 测试方式

为了快速测试, 使用 Spring Boot 做集成测试

只有一个测试功能, 因为不同的 Jedis 连接模式配置文件不一样, 因此使用最简单的方式, 即根据不同的环境加载不同的配置文件

因此需要设置测试类的 Environment variables

此模式需要添加 

spring.profiles.acrive=standalone