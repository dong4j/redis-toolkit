package info.dong4j.redis.sharded.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

/**
 * <p>Description: sharded jedis 配置类 </p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  11:43
 */
@Slf4j
@Configuration
public class ShardJedisConfiguration {
    @Value("${redis.node}")
    private String  redisNode;
    @Value("${redis.connectionTimeout}")
    private int     connectionTimeout;
    /** 等待Response超时时间 */
    @Value("${redis.soTimeout}")
    private int     soTimeout;
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

    @ConditionalOnProperty(value = "redis.model", havingValue = "sharding")
    @Bean(name = "shardedJedisPool", destroyMethod = "destroy")
    public ShardedJedisPool shardedJedisPool() {
        return new ShardedJedisPool(jedisPoolConfig(), new ArrayList<JedisShardInfo>() {
            private static final long serialVersionUID = -6785278696454543117L;
            String[] nodes = redisNode.split(";");

            {
                for (String node : nodes) {
                    JedisShardInfo jedisShardInfo = new JedisShardInfo(node);
                    jedisShardInfo.setConnectionTimeout(connectionTimeout);
                    jedisShardInfo.setSoTimeout(soTimeout);
                    add(jedisShardInfo);
                }
            }
        });
    }
}
