package info.dong4j.redis.sharded.sentinel.config;

import info.dong4j.redis.sharded.sentinel.ShardedJedisSentinelPool;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.JedisURIHelper;

/**
 * <p>Description: sharded jedis sentinel 配置类</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  19:26
 */
@Slf4j
@Configuration
public class ShardSentinelJedisConfiguration {
    private static final int DEFAULT_MAX_REDIRECTIONS = 5;
    private static final int DEFAULT_DATABASE         = 0;

    public static final String COMMA               = ",";
    public static final String SEMICOLON           = ";";
    public static final String COLON               = ":";
    public static final String BUSINESS_SEPARATION = "#";
    public static final String AGREEMENT           = "redis";

    @Value("${redis.sentinel.node}")
    private String sentinelNode;

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

    // <!-- 连接池配置 -->
    // <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
    // 	<!-- 最大分配的对象数 -->
    // 	<property name="maxTotal" value="1024" />
    // 	<!-- 最大能够保持idel状态的对象数 -->
    // 	<property name="maxIdle" value="5000" />
    // 	<!-- 多长时间检查一次连接池中空闲的连接 -->
    // 	<property name="timeBetweenEvictionRunsMillis" value="1000" />
    // 	<!-- 空闲连接多长时间后会被收回 -->
    // 	<property name="minEvictableIdleTimeMillis" value="30000" />
    // 	<!-- 调用borrow 一个对象方法时，是否检查其有效性 -->
    // 	<property name="testOnBorrow" value="false" />
    // 	<!-- 调用return 一个对象方法时，是否检查其有效性 -->
    // 	<property name="testOnReturn" value="false" />
    // 	<!-- 在空闲时检查有效性, 默认false -->
    // 	<property name="testWhileIdle" value="true" />
    // </bean>

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

    // <!-- sentinel连接池对象 -->
    // <bean id="shardedJedisSentinelPool" class="redis.clients.jedis.ShardedJedisSentinelPool">
    // 	<!-- master-name -->
    // 	<constructor-arg index="0" name="masters">
    // 		<list>
    // 			<value>master1</value>
    // 			<value>master2</value>
    // 		</list>
    // 	</constructor-arg>
    // 	<!-- sentinel集群地址列表 -->
    // 	<constructor-arg index="1" name="sentinels">
    // 		<set>
    // 			<value>192.168.1.112:26379</value>
    // 			<value>192.168.1.112:26380</value>
    // 		</set>
    // 	</constructor-arg>
    // 	<!-- pool配置 -->
    // 	<constructor-arg index="2" ref="jedisPoolConfig" />
    // </bean>

    @ConditionalOnProperty(value = "redis.model", havingValue = "sharding-sentinel")
    @Bean(name = "shardedJedisSentinelPool", destroyMethod = "destroy")
    public ShardedJedisSentinelPool shardedJedisSentinelPool() {
        // mymaster#redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381;mymaster#redis://127.0.0.1:26379,redis://127.0.0.1:26380,redis://127.0.0.1:26381
        if (StringUtils.isBlank(sentinelNode)) {
            throw new RuntimeException("sentinel must to configure");
        }
        String[] nodes = sentinelNode.split(SEMICOLON);
        if (nodes.length == 1) {
            throw new RuntimeException("current redis model is sharding-sentinel, support multiple groups sentinel, if has one group sentinel, please use sentinel model for performance");
        }
        List<String> masterList  = new ArrayList<>(nodes.length);
        Set<String>  sentinelSet = new HashSet<>();
        String       password    = null;
        boolean      flag        = true;
        for (String node : nodes) {
            if (StringUtils.isNotBlank(node)) {
                String[] nodeInfo = node.split(BUSINESS_SEPARATION);
                if (nodeInfo.length != 2) {
                    throw new RuntimeException("please use pattern like masterName#redis://[password@]ip:port[/database]");
                }
                String masterName = nodeInfo[0];
                masterList.add(masterName);

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
        }

        return new ShardedJedisSentinelPool(masterList,
                                            sentinelSet,
                                            jedisPoolConfig(),
                                            soTimeout,
                                            DEFAULT_MAX_REDIRECTIONS,
                                            connectionTimeout,
                                            password,
                                            DEFAULT_DATABASE);

    }
}
