package info.dong4j.redis.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * <p>Description: 分片模式</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  10:44
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:/application-sharded-redis.xml"})
public class RedisShardedTest {
    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Test
    public void testShardedRedis() {
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setTest("testShardedRedis_" + i, "testShardedRedis_" + i);
            log.info("set testShardedRedis_{}", i);
        }
    }

    private void setTest(String key, String value) {
        try (ShardedJedis jedis = shardedJedisPool.getResource()) {
            jedis.set(key, value);
            log.info(jedis.get(key));
        } catch (Exception e) {
            log.error("set error", e);
        }
    }
}