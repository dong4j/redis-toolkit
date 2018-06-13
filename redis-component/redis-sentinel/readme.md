# sentinel 操作 redis

## 集成方式

通过 JavaConfig 配置 ShardedJedis 相关 bean 

## 使用方式

1. 引入包
2. 在配置文件中  import application-sentinel-redis.xml
3. 注入 JedisSentinelPool
 
## 测试方式

为了快速测试, 使用 Spring Boot 做集成测试

只有一个测试功能, 因为不同的 Jedis 连接模式配置文件不一样, 因此使用最简单的方式, 即根据不同的环境加载不同的配置文件

因此需要设置测试类的 Environment variables

此模式需要添加 

spring.profiles.acrive=sentinel

**配置文件为**

`application-sentinel.properties`

## 注意

哨兵模式不需要配置 master 的连接地址, 只需要配置哨兵的连接地址, 哨兵会通过配置和 master, slave 通信;
当 master 被一半以上的哨兵发现不可用, 将通过选举, 将一个 slave 提升为 master