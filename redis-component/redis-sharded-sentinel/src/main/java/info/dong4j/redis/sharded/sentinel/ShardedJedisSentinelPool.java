package info.dong4j.redis.sharded.sentinel;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Hashing;
import redis.clients.util.Pool;

/**
 * <p>Description: 具有分片功能的哨兵模式实现</p>
 *
 * @author dong4j
 * @email dong4j @gmail.com
 * @date 2018 -06-13  19:20
 */
@Slf4j
public class ShardedJedisSentinelPool extends Pool<ShardedJedis> {
    /**
     * 配置信息
     */
    private GenericObjectPoolConfig poolConfig;

    /**
     * sentinel 监听器，订阅sentinel集群上master变更的消息
     */
    private Set<MasterListener> masterListeners;

    /**
     * 本地master路由表
     */
    private volatile Map<String, HostAndPort> localMasterRoute = new ConcurrentHashMap<String, HostAndPort>();

    /**
     * 从sentinel获取master地址出错的重试次数
     */
    private int retrySentinel = 5;

    private int    connectionTimeout;
    private int    soTimeout;
    private int    database;
    private String password;

    /**
     * Instantiates a new Sharded jedis sentinel pool.
     *
     * @param masters   the masters
     * @param sentinels the sentinels
     */
    public ShardedJedisSentinelPool(List<String> masters, Set<String> sentinels) {
        this(masters, sentinels, new GenericObjectPoolConfig());
    }

    /**
     * Instantiates a new Sharded jedis sentinel pool.
     *
     * @param masters    the masters
     * @param sentinels  the sentinels
     * @param poolConfig the pool config
     */
    public ShardedJedisSentinelPool(List<String> masters, Set<String> sentinels,
                                    final GenericObjectPoolConfig poolConfig) {
        this(masters, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT);
    }

    /**
     * Instantiates a new Sharded jedis sentinel pool.
     *
     * @param masters    the masters
     * @param sentinels  the sentinels
     * @param poolConfig the pool config
     * @param soTimeout  the so timeout
     */
    public ShardedJedisSentinelPool(List<String> masters, Set<String> sentinels,
                                    final GenericObjectPoolConfig poolConfig, int soTimeout) {
        this(masters, sentinels, poolConfig, soTimeout, 5);
    }

    /**
     * Instantiates a new Sharded jedis sentinel pool.
     *
     * @param masters       the masters
     * @param sentinels     the sentinels
     * @param poolConfig    the pool config
     * @param soTimeout     the so timeout
     * @param retrySentinel the retry sentinel
     */
    public ShardedJedisSentinelPool(List<String> masters, Set<String> sentinels,
                                    GenericObjectPoolConfig poolConfig, int soTimeout, int retrySentinel) {
        this(masters, sentinels, poolConfig, soTimeout, retrySentinel, Protocol.DEFAULT_TIMEOUT,
             null, Protocol.DEFAULT_DATABASE);
    }

    /**
     * Instantiates a new Sharded jedis sentinel pool.
     *
     * @param masters           the masters
     * @param sentinels         the sentinels
     * @param poolConfig        the pool config
     * @param soTimeout         the so timeout
     * @param retrySentinel     the retry sentinel
     * @param connectionTimeout the connection timeout
     * @param password          the password
     * @param database          the database
     */
    public ShardedJedisSentinelPool(List<String> masters, Set<String> sentinels,
                                    final GenericObjectPoolConfig poolConfig, int soTimeout, int retrySentinel,
                                    int connectionTimeout, final String password, final int database) {
        this.poolConfig = poolConfig;
        this.soTimeout = soTimeout;
        this.retrySentinel = retrySentinel;
        this.connectionTimeout = connectionTimeout;
        this.password = password;
        this.database = database;
        this.masterListeners = new HashSet<MasterListener>(sentinels.size());

        Map<String, HostAndPort> newMasterRoute = initSentinels(sentinels, masters);
        initPool(newMasterRoute);
    }

    /**
     * 根据newMasterRoute路由表信息初始化连接池
     *
     * @param newMasterRoute the new master route
     */
    private void initPool(Map<String, HostAndPort> newMasterRoute) {
        if (!equals(localMasterRoute, newMasterRoute)) {
            List<JedisShardInfo> shardMasters = makeShardInfoList(newMasterRoute);
            initPool(poolConfig, new ShardedJedisFactory(shardMasters, Hashing.MURMUR_HASH, null));
            localMasterRoute.putAll(newMasterRoute);
        }
    }

    /**
     * 获取ShardedJedis客户端
     */
    @Override
    public ShardedJedis getResource() {
        ShardedJedis jedis = null;
        try {
            jedis = super.getResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jedis != null) {
            jedis.setDataSource(this);
        }
        return jedis;
    }

    /**
     * 为了兼容老代码调用，直接从ShardedJedisPool中拷贝而来。
     *
     * @deprecated starting from Jedis 3.0 this method will not be exposed. Resource cleanup should be done using @see
     *     {@link redis.clients.jedis.Jedis#close()}
     */
    @Override
    @Deprecated
    public void returnBrokenResource(final ShardedJedis resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    /**
     * 为了兼容老代码调用，直接从ShardedJedisPool中拷贝而来。
     *
     * @deprecated starting from Jedis 3.0 this method will not be exposed. Resource cleanup should be done using @see
     *     {@link redis.clients.jedis.Jedis#close()}
     */
    @Override
    @Deprecated
    public void returnResource(final ShardedJedis resource) {
        if (resource != null) {
            resource.resetState();
            returnResourceObject(resource);
        }
    }

    /**
     * close
     */
    @Override
    public void destroy() {
        for (MasterListener m : masterListeners) {
            m.shutdown();
        }
        super.destroy();
    }

    /**
     * 本地路由表与新路由表对比
     *
     * @param localMasterRoute the local master route
     * @param newMasterRoute   the new master route
     * @return boolean boolean
     */
    private boolean equals(Map<String, HostAndPort> localMasterRoute,
                           Map<String, HostAndPort> newMasterRoute) {
        if (localMasterRoute != null && newMasterRoute != null) {
            if (localMasterRoute.size() == newMasterRoute.size()) {
                Set<String> keySet = newMasterRoute.keySet();
                for (String key : keySet) {
                    if (!localMasterRoute.get(key).equals(newMasterRoute.get(key))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前活跃地址
     *
     * @return the current host master
     */
    public List<HostAndPort> getCurrentHostMaster() {
        return toHostAndPort(localMasterRoute);
    }

    /**
     * To host and port list.
     *
     * @param routingTable the routing table
     * @return list
     */
    private List<HostAndPort> toHostAndPort(Map<String, HostAndPort> routingTable) {
        return new ArrayList<>(routingTable.values());
    }

    /**
     * To host and port host and port.
     *
     * @param masterAddr the master addr
     * @return host and port
     */
    private HostAndPort toHostAndPort(List<String> masterAddr) {
        String host = masterAddr.get(0);
        int    port = Integer.parseInt(masterAddr.get(1));
        return new HostAndPort(host, port);
    }

    /**
     * 构造JedisShardInfo
     *
     * @param newMasterRoute the new master route
     * @return list
     */
    private List<JedisShardInfo> makeShardInfoList(Map<String, HostAndPort> newMasterRoute) {
        List<JedisShardInfo>            shardMasters = new ArrayList<JedisShardInfo>();
        Set<Entry<String, HostAndPort>> entrySet     = newMasterRoute.entrySet();
        StringBuilder                   info         = new StringBuilder();
        for (Map.Entry<String, HostAndPort> entry : entrySet) {
            // 这个里带上master-name(entry.getKey())作为JedisShardInfo的name
            // 以便同一个master一致性hash时落在相同的点上,详情可参考redis.clients.util.Sharded.getShard(String key)
            JedisShardInfo jedisShardInfo = new JedisShardInfo(entry.getValue().getHost(), entry
                .getValue().getPort(), soTimeout, entry.getKey());
            jedisShardInfo.setPassword(password);
            shardMasters.add(jedisShardInfo);

            info.append(entry.getKey());
            info.append(":");
            info.append(entry.getValue().toString());
            info.append(" ");
        }
        log.info("Created ShardedJedisPool to master at [" + info.toString() + "]");
        return shardMasters;
    }

    /**
     * 初始化Sentinels，获取master路由表信息
     *
     * @param sentinels the sentinels
     * @param masters   the masters
     * @return map
     */
    private Map<String, HostAndPort> initSentinels(Set<String> sentinels, final List<String> masters) {

        log.info("Trying to find all master from available Sentinels...");

        Map<String, HostAndPort> masterRoute = new LinkedHashMap<String, HostAndPort>();

        for (String masterName : masters) {
            HostAndPort master = masterRoute.get(masterName);
            // 当前master已初始化
            if (null != master) {
                continue;
            }

            boolean fetched            = false;
            boolean sentinelAvailable  = false;
            int     sentinelRetryCount = 0;

            while (!fetched && sentinelRetryCount < retrySentinel) {
                for (String sentinel : sentinels) {
                    final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));

                    log.info("Connecting to Sentinel " + hap);

                    Jedis jedis = null;
                    try {
                        jedis = new Jedis(hap.getHost(), hap.getPort());
                        // 从sentinel获取masterName当前master-host地址
                        List<String> masterAddr = jedis.sentinelGetMasterAddrByName(masterName);
                        // connected to sentinel...
                        sentinelAvailable = true;

                        if (masterAddr == null || masterAddr.size() != 2) {
                            log.warn("Can not get master addr, master name: " + masterName
                                     + ". Sentinel: " + hap + ".");
                            continue;
                        }

                        // 将masterName-master存入路由表
                        master = toHostAndPort(masterAddr);
                        log.info("Found Redis master at " + master);

                        masterRoute.put(masterName, master);
                        fetched = true;
                        jedis.disconnect();
                        break;
                    } catch (JedisConnectionException e) {
                        log.warn("Cannot connect to sentinel running @ " + hap + ". Trying next one.");
                    } finally {
                        try {
                            if (jedis != null) {
                                jedis.disconnect();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                if (null == master) {
                    try {
                        if (sentinelAvailable) {
                            // can connect to sentinel, but master name seems to not
                            // monitored
                            throw new JedisException("Can connect to sentinel, but " + masterName
                                                     + " seems to be not monitored...");
                        } else {
                            log.info("All sentinels down, cannot determine where is "
                                     + masterName
                                     + " master is running... sleeping 1000ms, Will try again.");
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fetched = false;
                    sentinelRetryCount++;
                }
            }

            // Try sentinelRetry times.
            if (!fetched && sentinelRetryCount >= retrySentinel) {
                log.info("All sentinels down and try " + sentinelRetryCount + " times, Abort.");
                throw new JedisConnectionException("Cannot connect all sentinels, Abort.");
            }
        }

        log.info("Redis master running at " + masterRoute.size()
                 + ", starting Sentinel listeners...");
        for (String sentinel : sentinels) {
            final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));
            MasterListener masterListener = new MasterListener(masters, hap.getHost(),
                                                               hap.getPort());
            // whether MasterListener threads are alive or not, process can be stopped
            masterListener.setDaemon(true);
            masterListeners.add(masterListener);
            masterListener.start();
        }

        return masterRoute;
    }

    /**
     * <p>Description: master监听器，从sentinel订阅master变更的消息 </p>
     *
     * @author dong4j
     * @email dong4j@gmail.com
     * @date 2018-06-13  20:09
     */
    protected class MasterListener extends Thread {

        /**
         * The Masters.
         */
        protected List<String> masters;
        /**
         * The Host.
         */
        protected String       host;
        /**
         * The Port.
         */
        protected int          port;
        /**
         * The Subscribe retry wait time millis.
         */
        protected long subscribeRetryWaitTimeMillis = 5000;
        /**
         * The J.
         */
        protected volatile Jedis j;
        /**
         * The Running.
         */
        protected AtomicBoolean running = new AtomicBoolean(false);

        /**
         * Instantiates a new Master listener.
         */
        protected MasterListener() {
        }

        /**
         * Instantiates a new Master listener.
         *
         * @param masters the masters
         * @param host    the host
         * @param port    the port
         */
        public MasterListener(List<String> masters, String host, int port) {
            super(String.format("MasterListener-%s-[%s:%d]", masters, host, port));
            this.masters = masters;
            this.host = host;
            this.port = port;
        }

        /**
         * Instantiates a new Master listener.
         *
         * @param masters                      the masters
         * @param host                         the host
         * @param port                         the port
         * @param subscribeRetryWaitTimeMillis the subscribe retry wait time millis
         */
        public MasterListener(List<String> masters, String host, int port,
                              long subscribeRetryWaitTimeMillis) {
            this(masters, host, port);
            this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
        }

        @Override
        public void run() {
            running.set(true);
            while (running.get()) {
                // Sentinel可能发生宕机，因此try-catch这一步是必须的.
                try {
                    j = new Jedis(host, port);
                    // 订阅master变更消息
                    j.subscribe(new MasterChengeProcessor(this.masters, this.host, this.port), "+switch-master");
                } catch (JedisConnectionException e) {
                    if (running.get()) {
                        log.info("Lost connection to Sentinel at " + host + ":" + port
                                 + ". Sleeping 5000ms and retrying.");
                        try {
                            Thread.sleep(subscribeRetryWaitTimeMillis);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        log.info("Unsubscribing from Sentinel at " + host + ":" + port);
                    }
                }
            }
        }

        /**
         * Shutdown.
         */
        public void shutdown() {
            try {
                log.info("Shutting down listener on " + host + ":" + port);
                running.set(false);
                // This isn't good, the Jedis object is not thread safe
                if (j != null) {
                    j.disconnect();
                }
            } catch (Exception e) {
                log.error("Caught exception while shutting down", e);
            }
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MasterListener that = (MasterListener) o;
            return port == that.port && Objects.equals(host, that.host);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port);
        }
    }

    /**
     * master变更时初始化连接池更新锁
     */
    private ConcurrentHashMap<String, HostAndPort> updatePoolLock = new ConcurrentHashMap<String, HostAndPort>();

    /**
     * <p>Description: 当master变更时接收消息处理 </p>
     *
     * @author dong4j
     * @email dong4j@gmail.com
     * @date 2018-06-13  20:10
     */
    protected class MasterChengeProcessor extends JedisPubSub {

        /**
         * The Masters.
         */
        protected List<String> masters;
        /**
         * The Host.
         */
        protected String       host;
        /**
         * The Port.
         */
        protected int          port;

        /**
         * Instantiates a new Master chenge processor.
         *
         * @param masters the masters
         * @param host    the host
         * @param port    the port
         */
        public MasterChengeProcessor(List<String> masters, String host, int port) {
            super();
            this.masters = masters;
            this.host = host;
            this.port = port;
        }


        /**
         * On message.
         *
         * @param channel the channel
         * @param message the message
         */
        @Override
        public void onMessage(String channel, String message) {
            masterChengeProcessor(channel, message);
        }

        /**
         * master变更消息处理
         * message格式：master-name old-master-host old-master-port new-master-host new-master-port
         * 示例：master1 192.168.1.112 6380 192.168.1.111 6379
         */
        private void masterChengeProcessor(String channel, String message) {
            log.info("Sentinel " + host + ":" + port + " published: " + message + ".");
            String[] switchMasterMsg = message.split(" ");
            if (switchMasterMsg.length > 3) {

                String chengeMasterName = switchMasterMsg[0];
                HostAndPort newHostMaster = toHostAndPort(Arrays.asList(switchMasterMsg[3],
                                                                        switchMasterMsg[4]));
                boolean lock = lock(chengeMasterName, newHostMaster);
                try {
                    if (lock) {
                        // 拷贝本地路由表
                        Map<String, HostAndPort> newMasterRoute = new LinkedHashMap<>(
                            localMasterRoute);
                        // 设置变更信息
                        newMasterRoute.put(chengeMasterName, newHostMaster);

                        log.info("Sentinel " + host + ":" + port + " start update...");
                        // 防止二次更新
                        synchronized (MasterChengeProcessor.class) {
                            // 重新初始化pool
                            initPool(newMasterRoute);
                        }
                    } else {
                        StringBuilder info = new StringBuilder();
                        for (String masterName : masters) {
                            info.append(masterName);
                            info.append(",");
                        }
                        log.info("Ignoring message on +switch-master for master name "
                                 + switchMasterMsg[0] + ", our monitor master name are [" + info
                                 + "]");
                    }
                } finally {
                    if (lock) {
                        unLock(chengeMasterName, newHostMaster);
                    }
                }
            } else {
                log.info("Invalid message received on Sentinel " + host + ":" + port
                         + " on channel +switch-master: " + message);
            }
        }

        /**
         * 1.因sentinel集群能同时管理多组master-slave,故只处理当前工程配置的master变更
         * 2.如果变更的master信息已存在，并且对应ip一致，则为重复消息（放弃更新）
         * 3.如果变更的master信息已存在，不一致则为master变更（lock）
         *
         * @param chengeMasterName 变更的master-name
         * @param newHostMaster    新的master地址
         * @return boolean boolean
         */
        boolean lock(String chengeMasterName, HostAndPort newHostMaster) {
            int index = masters.indexOf(chengeMasterName);
            if (index == -1) {
                return false;
            }

            HostAndPort currentHostAndPort = localMasterRoute.get(chengeMasterName);
            if (newHostMaster.equals(currentHostAndPort)) {
                log.info("Sentinel " + host + ":" + port + " update " + chengeMasterName
                         + " failure! because Has been updated.");
                return false;
            }

            String      key         = String.format("%s-%s", chengeMasterName, newHostMaster);
            HostAndPort putIfAbsent = updatePoolLock.putIfAbsent(key, newHostMaster);
            if (null != putIfAbsent && newHostMaster.equals(putIfAbsent)) {
                log.info("Sentinel " + host + ":" + port + " lock " + chengeMasterName
                         + " failure! because Has been lock.");
                return false;
            }

            log.info("Sentinel " + host + ":" + port + " lock " + chengeMasterName
                     + " success! key:" + key);
            return true;
        }

        /**
         * 解除锁定
         *
         * @param chengeMasterName the chenge master name
         * @param newHostMaster    the new host master
         */
        void unLock(String chengeMasterName, HostAndPort newHostMaster) {
            String key = String.format("%s-%s", chengeMasterName, newHostMaster);
            updatePoolLock.remove(key);
            log.info("Sentinel " + host + ":" + port + " unlock " + chengeMasterName + " success.");
        }
    }

    /**
     * <p>Description: ShardedJedis生产工厂 </p>
     *
     * @author dong4j
     * @email dong4j@gmail.com
     * @date 2018-06-13  20:11
     */
    protected class ShardedJedisFactory implements PooledObjectFactory<ShardedJedis> {
        private List<JedisShardInfo> shards;
        private Hashing              algo;
        private Pattern              keyTagPattern;

        /**
         * Instantiates a new Sharded jedis factory.
         *
         * @param shards        the shards
         * @param algo          the algo
         * @param keyTagPattern the key tag pattern
         */
        public ShardedJedisFactory(List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern) {
            this.shards = shards;
            this.algo = algo;
            this.keyTagPattern = keyTagPattern;
        }

        /**
         * Create an instance that can be served by the pool and wrap it in a
         * {@link PooledObject} to be managed by the pool.
         *
         * @return a {@code PooledObject} wrapping an instance that can be served by the pool
         */
        @Override
        public PooledObject<ShardedJedis> makeObject() {
            ShardedJedis jedis = new ShardedJedis(shards, algo, keyTagPattern);
            return new DefaultPooledObject<>(jedis);
        }

        @Override
        public void destroyObject(PooledObject<ShardedJedis> pooledShardedJedis) {
            final ShardedJedis shardedJedis = pooledShardedJedis.getObject();
            for (Jedis jedis : shardedJedis.getAllShards()) {
                try {
                    try {
                        jedis.quit();
                    } catch (Exception e) {

                    }
                    jedis.disconnect();
                } catch (Exception e) {

                }
            }
        }


        /**
         * Validate objects boolean.
         * 默认验证方法
         *
         * @param pooledShardedJedis the pooled sharded jedis
         * @return the boolean
         */
        @Override
        public boolean validateObject(PooledObject<ShardedJedis> pooledShardedJedis) {
            try {
                ShardedJedis jedis = pooledShardedJedis.getObject();
                for (Jedis shard : jedis.getAllShards()) {
                    if (!shard.ping().equals("PONG")) {
                        return false;
                    }
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public void activateObject(PooledObject<ShardedJedis> p) {

        }

        @Override
        public void passivateObject(PooledObject<ShardedJedis> p) {

        }
    }
}
