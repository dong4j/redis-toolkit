package info.dong4j.redis.integration;

import info.dong4j.redis.sharded.sentinel.ShardedJedisSentinelPool;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * <p>Description: sentinel 测试</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  14:58
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:/application-sharded-sentinel-redis.xml"})
public class RedisShardedSentinelTest {
    @Autowired
    private ShardedJedisSentinelPool shardedJedisSentinelPool;

    @Test
    public void testShardedSentinelRedis() {
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setTest("testShardedSentinelRedis_" + i, "testShardedSentinelRedis_" + i);
            log.info("set testShardedSentinelRedis_{}", i);
        }
    }

    private void setTest(String key, String value) {
        try (ShardedJedis jedis = shardedJedisSentinelPool.getResource()) {
            jedis.set(key, value);
            log.info(jedis.get(key));
        } catch (Exception e) {
            log.error("set error", e);
        }
    }
}
