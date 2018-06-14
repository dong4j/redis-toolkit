package info.dong4j.redis.spring.sentinel.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.net.*;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.JedisURIHelper;

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
    private static final int    DEFAULT_MAX_REDIRECTIONS = 5;
    private static final int    DEFAULT_DATABASE         = 0;
    public static final  String COMMA                    = ",";
    public static final  String SEMICOLON                = ";";
    public static final  String COLON                    = ":";
    public static final  String BUSINESS_SEPARATION      = "#";
    public static final  String AGREEMENT                = "redis";
    private              String password                 = null;

    @Value("${redis.sentinel.node}")
    private String  sentinelNode;
    /** 等待Response超时时间 */
    @Value("${redis.soTimeout}")
    private int     soTimeout;
    @Value("${redis.connectionTimeout}")
    private int     connectionTimeout;
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
    @ConditionalOnProperty(value = "redis.model", havingValue = "spring-sentinel")
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        // redis.sentinel.node=mymaster#redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
        if (StringUtils.isBlank(sentinelNode)) {
            throw new RuntimeException("sentinel must to configure");
        }

        String[] nodes = sentinelNode.split(SEMICOLON);
        if (nodes.length > 1) {
            throw new RuntimeException("current redis model is spring-sentinel, cannot support multiple groups sentinel, please use spring-shard-sentinel model");
        }

        String node = nodes[0];

        // mymaster#redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
        String[] nodeInfo = node.split(BUSINESS_SEPARATION);
        if (!node.contains(BUSINESS_SEPARATION) || nodeInfo.length != 2) {
            throw new RuntimeException("please use pattern like masterName#redis://[password@]ip:port[/database]");
        }
        String masterName = nodeInfo[0];
        // redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
        String[] sentinels = nodeInfo[1].split(COMMA);
        boolean  flag      = true;
        for (String sentinel : sentinels) {
            URI uri;
            try {
                uri = new URI(sentinel);
            } catch (URISyntaxException e) {
                throw new RuntimeException("sentinel node analysis error, please use pattern like redis://[password@]ip:port[/database], sentinelNode = " + sentinelNode);
            }
            if (!Objects.equals(uri.getScheme(), AGREEMENT)) {
                throw new RuntimeException("please use [redis://] agreement");
            }
            String ps = JedisURIHelper.getPassword(uri);
            if (flag && StringUtils.isNotBlank(ps)) {
                password = ps;
                flag = false;
            }
            configuration.addSentinel(new RedisNode(uri.getHost(), uri.getPort()));
        }
        configuration.setMaster(masterName);
        return configuration;
    }

    /**
     * 连接redis的工厂类
     *
     * @return jedis connection factory
     */
    @Bean
    @ConditionalOnProperty(value = "redis.model", havingValue = "spring-sentinel")
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory(redisSentinelConfiguration(), jedisPoolConfig());
        factory.setTimeout(this.connectionTimeout);
        factory.setPassword(password);
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
    @ConditionalOnProperty(value = "redis.model", havingValue = "spring-sentinel")
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
    @ConditionalOnProperty(value = "redis.model", havingValue = "spring-sentinel")
    public CacheManager cacheManager() {
        return new RedisCacheManager(redisTemplate());
    }

    /**
     * 自定义生成redis-key
     */
    @Override
    @ConditionalOnProperty(value = "redis.model", havingValue = "spring-sentinel")
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
