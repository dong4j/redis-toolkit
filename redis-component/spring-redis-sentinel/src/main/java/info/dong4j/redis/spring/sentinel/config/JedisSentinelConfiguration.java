package info.dong4j.redis.spring.sentinel.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <p>Description: spring redis sentinel 配置类</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  14:49
 */
@Slf4j
@Configuration
public class JedisSentinelConfiguration extends CachingConfigurerSupport {
    @Value("${redis.password}")
    private String  password;
    @Value("${redis.connectionTimeout}")
    private int     timeout;
    @Value("${redis.sentinel.nodes}")
    private String  sentinelNodes;
    @Value("${redis.sentinel.master}")
    private String  master;
    @Value("${redis.pool.maxActive}")
    private int     maxTotal;
    @Value("${redis.pool.maxWait}")
    private long    maxWaitMillis;
    @Value("${redis.pool.maxIdle}")
    private int     maxIdle;
    @Value("${redis.pool.minIdleTime}")
    private int     minIdle;
    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${redis.pool.testOnReturn}")
    private boolean testOnReturn;

    /**
     * Jedis pool config jedis pool config.
     * 配置 JedisPoolConfig
     *
     * @return JedisPoolConfig实体 jedis pool config
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 连接池最大连接数（使用负值表示没有限制）
        jedisPoolConfig.setMaxTotal(this.maxTotal);
        // 连接池最大阻塞等待时间（使用负值表示没有限制）
        jedisPoolConfig.setMaxWaitMillis(this.maxWaitMillis);
        // 连接池中的最大空闲连接
        jedisPoolConfig.setMaxIdle(this.maxIdle);
        // 连接池中的最小空闲连接
        jedisPoolConfig.setMinIdle(this.minIdle);
        // 当调用borrow Object方法时，是否进行有效性检查
        jedisPoolConfig.setTestOnBorrow(this.testOnBorrow);
        jedisPoolConfig.setTestOnReturn(this.testOnReturn);
        return jedisPoolConfig;
    }

    /**
     * redis哨兵配置
     *
     * @return redis sentinel configuration
     */
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        String[]                   host          = this.sentinelNodes.split(",");
        for (String redisHost : host) {
            String[] item = redisHost.split(":");
            String   ip   = item[0];
            String   port = item[1];
            configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
        }
        configuration.setMaster(this.master);
        return configuration;
    }

    /**
     * 连接redis的工厂类
     *
     * @return jedis connection factory
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());
        factory.setTimeout(this.timeout);
        factory.setPassword(StringUtils.isBlank(this.password) ? null : this.password);
        return factory;
    }

    /**
     * 配置RedisTemplate
     * 设置添加序列化器
     * key 使用string序列化器
     * value 使用Json序列化器
     * 还有一种简答的设置方式，改变defaultSerializer对象的实现。
     *
     * @return redis template
     */
    @Bean
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> redisTemplate() {
        //StringRedisTemplate的构造方法中默认设置了stringSerializer
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //设置开启事务
        template.setEnableTransactionSupport(true);
        //set key serializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper                objectMapper                = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(jedisConnectionFactory());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 设置RedisCacheManager
     * 使用cache注解管理redis缓存
     */
    @Override
    @Bean
    public CacheManager cacheManager() {
        return new RedisCacheManager(redisTemplate());
    }

    /**
     * 自定义生成redis-key
     */
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName()).append(".");
                sb.append(method.getName()).append(".");
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
                System.out.println("keyGenerator=" + sb.toString());
                return sb.toString();
            }
        };
    }
}
