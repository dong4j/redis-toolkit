package info.dong4j.redis.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

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
@ContextConfiguration(locations = {"classpath:/application-sentinel-redis.xml"})
public class RedisSentinelTest {
    @Autowired
    private JedisSentinelPool jedisSentinelPool;

    @Test
    public void testJedisSentinelPool() {
        Jedis jedis = jedisSentinelPool.getResource();
        log.info(jedis.toString());
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jedis.set("testJedisSentinelPool_" + i, "testJedisSentinelPool_" + i);
            log.info("set testJedisSentinelPool_{}", i);
            log.info("get testJedisSentinelPool_{}, value = {}", i, jedis.get("testJedisSentinelPool_" + i));
        }
    }
}
