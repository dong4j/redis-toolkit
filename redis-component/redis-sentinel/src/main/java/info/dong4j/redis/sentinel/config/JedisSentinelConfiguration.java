package info.dong4j.redis.sentinel.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.JedisURIHelper;

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
    public static final String COMMA               = ",";
    public static final String SEMICOLON           = ";";
    public static final String COLON               = ":";
    public static final String BUSINESS_SEPARATION = "#";
    public static final String AGREEMENT           = "redis";

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


    // public JedisSentinelPool(String masterName, Set<String> sentinels,
    //                          final GenericObjectPoolConfig poolConfig, final int connectionTimeout, final int soTimeout,
    //                          final String password, final int database, final String clientName) {
    //     this.poolConfig = poolConfig;
    //     this.connectionTimeout = connectionTimeout;
    //     this.soTimeout = soTimeout;
    //     this.password = password;
    //     this.database = database;
    //     this.clientName = clientName;
    // }

    @ConditionalOnProperty(value = "redis.model", havingValue = "sentinel")
    @Bean(name = "jedisSentinelPool", destroyMethod = "destroy")
    public JedisSentinelPool jedisSentinelPool() {
        // mymaster#redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
        if (StringUtils.isBlank(sentinelNode)) {
            throw new RuntimeException("sentinel must to configure");
        }
        String[] nodes = sentinelNode.split(SEMICOLON);
        if (nodes.length > 1) {
            throw new RuntimeException("current redis model is sentinel, cannot support multiple groups sentinel, please use shard-sentinel model");
        }
        String node = nodes[0];

        String      masterName  = null;
        Set<String> sentinelSet = new HashSet<>();
        String      password    = null;
        boolean     flag        = true;
        if (StringUtils.isNotBlank(node)) {
            String[] nodeInfo = node.split(BUSINESS_SEPARATION);
            if (!node.contains(BUSINESS_SEPARATION) || nodeInfo.length != 2) {
                throw new RuntimeException("please use pattern like masterName#redis://[password@]ip:port[/database]");
            }
            masterName = nodeInfo[0];
            // redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
            String[] sentinels = nodeInfo[1].split(COMMA);
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
                sentinelSet.add(uri.getHost() + COLON + uri.getPort());
                String ps = JedisURIHelper.getPassword(uri);
                if (flag && StringUtils.isNotBlank(ps)) {
                    password = ps;
                    flag = false;
                }
            }
        }

        return new JedisSentinelPool(masterName,
                                     sentinelSet,
                                     jedisPoolConfig(),
                                     this.connectionTimeout,
                                     password);
    }
}
