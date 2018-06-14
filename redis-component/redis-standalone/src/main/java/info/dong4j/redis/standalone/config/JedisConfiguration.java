package info.dong4j.redis.standalone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.*;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.JedisURIHelper;

/**
 * <p>Description: redis 配置类</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  10:24
 */
@Slf4j
@Configuration
public class JedisConfiguration {
    @Value("${redis.node}")
    private String  redisNode;
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
     * Jedis pool jedis pool.
     *
     * @return the jedis pool
     */
    @ConditionalOnProperty(value = "redis.model", havingValue = "standalone")
    @Bean(name = "jedisPool", destroyMethod = "destroy")
    public JedisPool jedisPool() {

        URI uri;
        try {
            uri = new URI(redisNode);
        } catch (URISyntaxException e) {
            throw new RuntimeException("redis node config error");
        }
        return new JedisPool(jedisPoolConfig(),
                             uri.getHost(),
                             uri.getPort(),
                             this.connectionTimeout,
                             JedisURIHelper.getPassword(uri));
    }
}
