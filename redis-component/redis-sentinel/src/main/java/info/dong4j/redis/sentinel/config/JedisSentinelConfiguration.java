package info.dong4j.redis.sentinel.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * <p>Description: redis sentinel 配置类</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  14:49
 */
@Slf4j
@Configuration
public class JedisSentinelConfiguration {
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

    @Bean(name = "jedisSentinelPool", destroyMethod = "destroy")
    public JedisSentinelPool jedisSentinelPool() {
        String[] host = this.sentinelNodes.split(",");
        return new JedisSentinelPool(this.master,
                                     new HashSet<>(Arrays.asList(host)),
                                     jedisPoolConfig(),
                                     this.timeout,
                                     StringUtils.isBlank(this.password) ? null : this.password);
    }
}
