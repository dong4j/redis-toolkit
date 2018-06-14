package info.dong4j.redis.cluster.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <p>Description: redis 集群整合 </p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-14  10:13
 */
@Slf4j
@Configuration
public class JedisClusterConfiguration {

    @Value("${redis.cluster.nodes}")
    private String  redisNodes;
    @Value("${redis.password}")
    private String  password;
    @Value("${redis.connectionTimeout}")
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

    // <bean id="redisClient" class="redis.clients.jedis.JedisCluster">
    //     <constructor-arg name="nodes">
    //         <set>
    //             <bean class="redis.clients.jedis.HostAndPort">
    //                 <constructor-arg name="host" value="192.168.31.100"/>
    //                 <constructor-arg name="port" value="7001"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.HostAndPort">
    //                 <constructor-arg name="host" value="192.168.31.100"/>
    //                 <constructor-arg name="port" value="7002"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.HostAndPort">
    //                 <constructor-arg name="host" value="192.168.31.100"/>
    //                 <constructor-arg name="port" value="7003"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.HostAndPort">
    //                 <constructor-arg name="host" value="192.168.31.100"/>
    //                 <constructor-arg name="port" value="7004"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.HostAndPort">
    //                 <constructor-arg name="host" value="192.168.31.100"/>
    //                 <constructor-arg name="port" value="7005"/>
    //             </bean>
    //             <bean class="redis.clients.jedis.HostAndPort">
    //                 <constructor-arg name="host" value="192.168.31.100"/>
    //                 <constructor-arg name="port" value="7006"/>
    //             </bean>
    //         </set>
    //     </constructor-arg>
    //     <constructor-arg name="poolConfig" ref="jedisPoolConfig"/>
    // </bean>

    @Bean
    public JedisCluster jedisCluster() {
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        HostAndPort      hostAndPort;
        // ip:port,ip:port
        String[] hostAndPorts = redisNodes.split(",");
        for (String hostAndPortString : hostAndPorts) {
            String[] info = hostAndPortString.split(":");
            hostAndPort = new HostAndPort(info[0], Integer.parseInt(info[1]));
            hostAndPortSet.add(hostAndPort);
        }
        return new JedisCluster(hostAndPortSet,
                                timeout,
                                10000,
                                5,
                                StringUtils.isBlank(password) ? null : password,
                                jedisPoolConfig());
    }
}
