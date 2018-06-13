package info.dong4j.redis.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>Description: 单机模式</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  10:44
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:/application-standalone-redis.xml"})
public class RedisStandaloneTest {
    @Autowired
    private JedisPool jedisPool;

    @Test
    public void testStandaloneRedis() {
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setTest("testStandaloneRedis_" + i, "testStandaloneRedis_" + i);
            log.info("set testStandaloneRedis_{}", i);
        }
    }

    private void setTest(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            log.info(jedis.get(key));
        } catch (Exception e) {
            log.error("set error", e);
        }
    }

    private void setTest1(String key, String value) {
        Jedis jedis = null;
        try {
            // 从连接池获取一个Jedis实例
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            log.info(jedis.get(key));
        } catch (Exception e) {
            log.error("set error", e);
        } finally {
            if (null != jedis) {
                // 释放资源还给连接池
                jedis.close();
            }
        }
    }
}