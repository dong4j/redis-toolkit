package info.dong4j.redis.sentinel.config;


import org.junit.Test;

/**
 * <p>Company: 科大讯飞股份有限公司-四川分公司</p>
 * <p>Description: ${description}</p>
 *
 * @author dong4j
 * @date 2018-06-14 19:54
 * @email sjdong3@iflytek.com
 */
public class JedisSentinelConfigurationTest {
    @Test
    public void test() {
        String   node      = ";";
        String[] nodeSplit = node.split(";");
        System.out.println(nodeSplit.length);
        System.out.println(nodeSplit[0]);

    }
}