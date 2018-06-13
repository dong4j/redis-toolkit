package info.dong4j.redis.integration;

import info.dong4j.redis.standalone.config.JedisConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>Description: </p>
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
        Jedis jedis = jedisPool.getResource();
        log.info(jedis.info());
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jedis.set("testStandaloneRedis_" + i, "testStandaloneRedis_" + i);
            log.info("set testStandaloneRedis_{}", i);
            log.info("get testStandaloneRedis_{}, value = {}", i, jedis.get("testStandaloneRedis_" + i));
        }
    }
}