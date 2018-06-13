package info.dong4j.redis.sharded.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.*;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  11:43
 */
@Slf4j
@Configuration
public class JedisConfiguration {
    /** redis://password@ip:port/database */
    @Value("${redis.uri.0}")
    private URI     uri0;
    @Value("${redis.uri.1}")
    private URI     uri1;
    @Value("${redis.timeout}")
    private int     timeout;
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
        log.debug("fkh-base-redis: init JedisPoolConfig instance");
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

    // <bean id="shardedJedisPool" class="redis.clients.jedis.ShardedJedisPool">
    //     <constructor-arg index="0" ref="jedisPoolConfig" />
    //     <constructor-arg index="1">
    //         <list>
    //             <bean class="redis.clients.jedis.JedisShardInfo">
    //                 <!-- jedis URI配置   redis://password@ip:port/database   -->
    //                 <constructor-arg value="redis://redis:@127.0.0.1:6379/0" type="java.net.URI"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.JedisShardInfo">
    //                 <!-- jedis URI配置   redis://redis:password@ip:port/database   -->
    //                 <constructor-arg value="redis://redis:@127.0.0.1:6380/0" type="java.net.URI"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.JedisShardInfo">
    //                 <!-- jedis URI配置   redis://redis:password@ip:port/database   -->
    //                 <constructor-arg value="redis://redis:@127.0.0.1:6381/0" type="java.net.URI"/>
    //             </bean>
    //         </list>
    //     </constructor-arg>
    // </bean>

    @Bean(name = "shardedJedisPool", destroyMethod = "destroy")
    public ShardedJedisPool shardedJedisPool() {
        return new ShardedJedisPool(jedisPoolConfig(), new ArrayList<JedisShardInfo>() {
            private static final long serialVersionUID = -6785278696454543117L;

            {
                add(new JedisShardInfo(uri0));
                add(new JedisShardInfo(uri1));
            }
        });
    }
}
