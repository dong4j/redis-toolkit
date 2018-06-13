package info.dong4j.redis.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath*:/application-spring-redis.xml"})
public class SpringRedisSentinelTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSpringRedisSentinel() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("hello", "testSpringRedisSentinel");
        log.error(valueOperations.get("hello").toString());

        User user = new User();
        user.name = "dong4j";

        valueOperations.set("whomai", user);
        log.error(valueOperations.get("whomai").toString());

        Collection<RedisServer> masterServers = redisTemplate.getConnectionFactory().getSentinelConnection().masters();
        for (RedisServer masterServer : masterServers) {
            log.error(masterServer.asString());
            log.error("host = {}, port = {}", masterServer.getHost(), masterServer.getPort());
            RedisNode redisNode = new RedisNode(masterServer.getHost(), masterServer.getPort());
            redisNode.setName(masterServer.getName());
            redisNode.setId(masterServer.getId());
            Collection<RedisServer> slaveServers = redisTemplate.getConnectionFactory().getSentinelConnection().slaves(redisNode);
            for (RedisServer slaveServer : slaveServers) {
                log.error(slaveServer.asString());
            }
        }


        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            valueOperations.set("testSpringRedisSentinel_" + i, "testSpringRedisSentinel_" + i);
            log.info("set testSpringRedisSentinel_{}", i);
            log.info("get testSpringRedisSentinel_{}, value = {}", i, valueOperations.get("testSpringRedisSentinel_" + i));
        }
    }
}

class User {
    @Override
    public String toString() {
        return "User{" +
               "name='" + name + '\'' +
               '}';
    }

    public String name;
}