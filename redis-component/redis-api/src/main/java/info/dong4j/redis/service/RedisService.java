package info.dong4j.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

/**
 * <p>Description: 对外提供的 api</p>
 *
 * @author dong4j
 * @email dong4j@gmail.com
 * @date 2018-06-13  10:42
 */
public interface RedisService {
    /**
     * Gets jedis by key.
     *
     * @param flag the flag
     * @return the jedis by key
     * @throws Exception the exception
     */
    Jedis getJedisByKey(String flag) throws Exception;

    /**
     * Return jedis by key.
     *
     * @param flag  the flag
     * @param jedis the jedis
     * @throws Exception the exception
     */
    void returnJedisByKey(String flag, Jedis jedis) throws Exception;

    /**
     * Gets sharded jedis by key.
     *
     * @param flag the flag
     * @return the sharded jedis by key
     * @throws Exception the exception
     */
    ShardedJedis getShardedJedisByKey(String flag) throws Exception;

    /**
     * Return sharded jedis by key.
     *
     * @param flag  the flag
     * @param jedis the jedis
     * @throws Exception the exception
     */
    void returnShardedJedisByKey(String flag, ShardedJedis jedis) throws Exception;

    /**
     * Append long.
     *
     * @param flag  the flag
     * @param key   the key
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    Long append(String flag, String key, String value) throws Exception;

    /**
     * Blpop list.
     *
     * @param flag    the flag
     * @param timeout the timeout
     * @param keys    the keys
     * @return the list
     * @throws Exception the exception
     */
    List<String> blpop(String flag, int timeout, String... keys) throws Exception;

    /**
     * Brpop list.
     *
     * @param flag    the flag
     * @param timeout the timeout
     * @param keys    the keys
     * @return the list
     * @throws Exception the exception
     */
    List<String> brpop(String flag, int timeout, String... keys) throws Exception;

    /**
     * Config get list.
     *
     * @param flag    the flag
     * @param pattern the pattern
     * @return the list
     * @throws Exception the exception
     */
    List<String> configGet(String flag, String pattern) throws Exception;

    /**
     * Config set string.
     *
     * @param flag      the flag
     * @param parameter the parameter
     * @param value     the value
     * @return the string
     * @throws Exception the exception
     */
    String configSet(String flag, String parameter, String value) throws Exception;

    /**
     * Decr long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long decr(String flag, String key) throws Exception;

    /**
     * Decr by long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param integer the integer
     * @return the long
     * @throws Exception the exception
     */
    Long decrBy(String flag, String key, long integer) throws Exception;

    /**
     * Del long.
     *
     * @param flag the flag
     * @param keys the keys
     * @return the long
     * @throws Exception the exception
     */
    Long del(String flag, String... keys) throws Exception;

    /**
     * Eval object.
     *
     * @param flag   the flag
     * @param script the script
     * @return the object
     * @throws Exception the exception
     */
    Object eval(String flag, String script) throws Exception;

    /**
     * Exists boolean.
     *
     * @param flag the flag
     * @param key  the key
     * @return the boolean
     * @throws Exception the exception
     */
    Boolean exists(String flag, String key) throws Exception;

    /**
     * Expire long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param seconds the seconds
     * @return the long
     * @throws Exception the exception
     */
    Long expire(String flag, String key, int seconds) throws Exception;

    /**
     * Flush all string.
     *
     * @param flag the flag
     * @return the string
     * @throws Exception the exception
     */
    String flushAll(String flag) throws Exception;

    /**
     * Flush db string.
     *
     * @param flag the flag
     * @return the string
     * @throws Exception the exception
     */
    String flushDB(String flag) throws Exception;

    /**
     * Get string.
     *
     * @param flag the flag
     * @param key  the key
     * @return the string
     * @throws Exception the exception
     */
    String get(String flag, String key) throws Exception;

    /**
     * Gets .
     *
     * @param flag   the flag
     * @param key    the key
     * @param offset the offset
     * @return the
     * @throws Exception the exception
     */
    Boolean getbit(String flag, String key, long offset) throws Exception;

    /**
     * Gets .
     *
     * @param flag        the flag
     * @param key         the key
     * @param startOffset the start offset
     * @param endOffset   the end offset
     * @return the
     * @throws Exception the exception
     */
    String getrange(String flag, String key, long startOffset, long endOffset) throws Exception;

    /**
     * Gets set.
     *
     * @param flag  the flag
     * @param key   the key
     * @param value the value
     * @return the set
     * @throws Exception the exception
     */
    String getSet(String flag, String key, String value) throws Exception;

    /**
     * Hdel long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param fields the fields
     * @return the long
     * @throws Exception the exception
     */
    Long hdel(String flag, String key, String... fields) throws Exception;

    /**
     * Hexists boolean.
     *
     * @param flag  the flag
     * @param key   the key
     * @param field the field
     * @return the boolean
     * @throws Exception the exception
     */
    Boolean hexists(String flag, String key, String field) throws Exception;

    /**
     * Hget string.
     *
     * @param flag  the flag
     * @param key   the key
     * @param field the field
     * @return the string
     * @throws Exception the exception
     */
    String hget(String flag, String key, String field) throws Exception;

    /**
     * Hget all map.
     *
     * @param flag the flag
     * @param key  the key
     * @return the map
     * @throws Exception the exception
     */
    Map<String, String> hgetAll(String flag, String key) throws Exception;

    /**
     * Hincr by long.
     *
     * @param flag  the flag
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    Long hincrBy(String flag, String key, String field, long value) throws Exception;

    /**
     * Hkeys set.
     *
     * @param flag the flag
     * @param key  the key
     * @return the set
     * @throws Exception the exception
     */
    Set<String> hkeys(String flag, String key) throws Exception;

    /**
     * Hlen long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long hlen(String flag, String key) throws Exception;

    /**
     * Hmget list.
     *
     * @param flag   the flag
     * @param key    the key
     * @param fields the fields
     * @return the list
     * @throws Exception the exception
     */
    List<String> hmget(String flag, String key, String... fields) throws Exception;

    /**
     * Hmset string.
     *
     * @param flag the flag
     * @param key  the key
     * @param hash the hash
     * @return the string
     * @throws Exception the exception
     */
    String hmset(String flag, String key, Map<String, String> hash) throws Exception;

    /**
     * Hset long.
     *
     * @param flag  the flag
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    Long hset(String flag, String key, String field, String value) throws Exception;

    /**
     * Hsetnx long.
     *
     * @param flag  the flag
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    Long hsetnx(String flag, String key, String field, String value) throws Exception;

    /**
     * Hvals list.
     *
     * @param flag the flag
     * @param key  the key
     * @return the list
     * @throws Exception the exception
     */
    List<String> hvals(String flag, String key) throws Exception;

    /**
     * Incr long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long incr(String flag, String key) throws Exception;

    /**
     * Incr by long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param integer the integer
     * @return the long
     * @throws Exception the exception
     */
    Long incrBy(String flag, String key, long integer) throws Exception;

    /**
     * Keys set.
     *
     * @param flag    the flag
     * @param pattern the pattern
     * @return the set
     * @throws Exception the exception
     */
    Set<String> keys(String flag, String pattern) throws Exception;

    /**
     * Lindex string.
     *
     * @param flag  the flag
     * @param key   the key
     * @param index the index
     * @return the string
     * @throws Exception the exception
     */
    String lindex(String flag, String key, long index) throws Exception;

    /**
     * Llen long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long llen(String flag, String key) throws Exception;

    /**
     * Lpop string.
     *
     * @param flag the flag
     * @param key  the key
     * @return the string
     * @throws Exception the exception
     */
    String lpop(String flag, String key) throws Exception;

    /**
     * Lpush long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param strings the strings
     * @return the long
     * @throws Exception the exception
     */
    Long lpush(String flag, String key, String... strings) throws Exception;

    /**
     * Lpushx long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param string the string
     * @return the long
     * @throws Exception the exception
     */
    Long lpushx(String flag, String key, String string) throws Exception;

    /**
     * Lrange list.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the list
     * @throws Exception the exception
     */
    List<String> lrange(String flag, String key, long start, long end) throws Exception;

    /**
     * Lrem long.
     *
     * @param flag  the flag
     * @param key   the key
     * @param count the count
     * @param value the value
     * @return the long
     * @throws Exception the exception
     */
    Long lrem(String flag, String key, long count, String value) throws Exception;

    /**
     * Lset string.
     *
     * @param flag  the flag
     * @param key   the key
     * @param index the index
     * @param value the value
     * @return the string
     * @throws Exception the exception
     */
    String lset(String flag, String key, long index, String value) throws Exception;

    /**
     * Ltrim string.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the string
     * @throws Exception the exception
     */
    String ltrim(String flag, String key, long start, long end) throws Exception;

    /**
     * Mget list.
     *
     * @param flag the flag
     * @param keys the keys
     * @return the list
     * @throws Exception the exception
     */
    List<String> mget(String flag, String... keys) throws Exception;

    /**
     * Move long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param dbIndex the db index
     * @return the long
     * @throws Exception the exception
     */
    Long move(String flag, String key, int dbIndex) throws Exception;

    /**
     * Mset string.
     *
     * @param flag       the flag
     * @param keysvalues the keysvalues
     * @return the string
     * @throws Exception the exception
     */
    String mset(String flag, String... keysvalues) throws Exception;

    /**
     * Msetnx long.
     *
     * @param flag       the flag
     * @param keysvalues the keysvalues
     * @return the long
     * @throws Exception the exception
     */
    Long msetnx(String flag, String... keysvalues) throws Exception;

    /**
     * Publish long.
     *
     * @param flag    the flag
     * @param channel the channel
     * @param message the message
     * @return the long
     * @throws Exception the exception
     */
    Long publish(String flag, String channel, String message) throws Exception;

    /**
     * Random key string.
     *
     * @param flag the flag
     * @return the string
     * @throws Exception the exception
     */
    String randomKey(String flag) throws Exception;

    /**
     * Rename string.
     *
     * @param flag   the flag
     * @param oldkey the oldkey
     * @param newkey the newkey
     * @return the string
     * @throws Exception the exception
     */
    String rename(String flag, String oldkey, String newkey) throws Exception;

    /**
     * Renamenx long.
     *
     * @param flag   the flag
     * @param oldkey the oldkey
     * @param newkey the newkey
     * @return the long
     * @throws Exception the exception
     */
    Long renamenx(String flag, String oldkey, String newkey) throws Exception;

    /**
     * Rpop string.
     *
     * @param flag the flag
     * @param key  the key
     * @return the string
     * @throws Exception the exception
     */
    String rpop(String flag, String key) throws Exception;

    /**
     * Rpoplpush string.
     *
     * @param flag   the flag
     * @param srckey the srckey
     * @param dstkey the dstkey
     * @return the string
     * @throws Exception the exception
     */
    String rpoplpush(String flag, String srckey, String dstkey) throws Exception;

    /**
     * Rpush long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param strings the strings
     * @return the long
     * @throws Exception the exception
     */
    Long rpush(String flag, String key, String... strings) throws Exception;

    /**
     * Rpushx long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param string the string
     * @return the long
     * @throws Exception the exception
     */
    Long rpushx(String flag, String key, String string) throws Exception;

    /**
     * Sadd long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param members the members
     * @return the long
     * @throws Exception the exception
     */
    Long sadd(String flag, String key, String... members) throws Exception;

    /**
     * Scard long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long scard(String flag, String key) throws Exception;

    /**
     * Sdiff set.
     *
     * @param flag the flag
     * @param keys the keys
     * @return the set
     * @throws Exception the exception
     */
    Set<String> sdiff(String flag, String... keys) throws Exception;

    /**
     * Sdiffstore long.
     *
     * @param flag   the flag
     * @param dstkey the dstkey
     * @param keys   the keys
     * @return the long
     * @throws Exception the exception
     */
    Long sdiffstore(String flag, String dstkey, String... keys) throws Exception;

    /**
     * Select string.
     *
     * @param flag  the flag
     * @param index the index
     * @return the string
     * @throws Exception the exception
     */
    String select(String flag, int index) throws Exception;

    /**
     * Set string.
     *
     * @param flag  the flag
     * @param key   the key
     * @param value the value
     * @return the string
     * @throws Exception the exception
     */
    String set(String flag, String key, String value) throws Exception;

    /**
     * Sets .
     *
     * @param flag   the flag
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the
     * @throws Exception the exception
     */
    Boolean setbit(String flag, String key, long offset, boolean value) throws Exception;

    /**
     * Sets .
     *
     * @param flag    the flag
     * @param key     the key
     * @param seconds the seconds
     * @param value   the value
     * @return the
     * @throws Exception the exception
     */
    String setex(String flag, String key, int seconds, String value) throws Exception;

    /**
     * Sets .
     *
     * @param flag  the flag
     * @param key   the key
     * @param value the value
     * @return the
     * @throws Exception the exception
     */
    Long setnx(String flag, String key, String value) throws Exception;

    /**
     * Sets .
     *
     * @param flag   the flag
     * @param key    the key
     * @param offset the offset
     * @param value  the value
     * @return the
     * @throws Exception the exception
     */
    Long setrange(String flag, String key, long offset, String value) throws Exception;

    /**
     * Sinter set.
     *
     * @param flag the flag
     * @param keys the keys
     * @return the set
     * @throws Exception the exception
     */
    Set<String> sinter(String flag, String... keys) throws Exception;

    /**
     * Smembers set.
     *
     * @param flag the flag
     * @param key  the key
     * @return the set
     * @throws Exception the exception
     */
    Set<String> smembers(String flag, String key) throws Exception;

    /**
     * Smove long.
     *
     * @param flag   the flag
     * @param srckey the srckey
     * @param dstkey the dstkey
     * @param member the member
     * @return the long
     * @throws Exception the exception
     */
    Long smove(String flag, String srckey, String dstkey, String member) throws Exception;

    /**
     * Sort list.
     *
     * @param flag the flag
     * @param key  the key
     * @return the list
     * @throws Exception the exception
     */
    List<String> sort(String flag, String key) throws Exception;

    /**
     * Sort list.
     *
     * @param flag              the flag
     * @param key               the key
     * @param sortingParameters the sorting parameters
     * @return the list
     * @throws Exception the exception
     */
    List<String> sort(String flag, String key, SortingParams sortingParameters) throws Exception;

    /**
     * Sort long.
     *
     * @param flag              the flag
     * @param key               the key
     * @param sortingParameters the sorting parameters
     * @param dstkey            the dstkey
     * @return the long
     * @throws Exception the exception
     */
    Long sort(String flag, String key, SortingParams sortingParameters, String dstkey) throws Exception;

    /**
     * Sort long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param dstkey the dstkey
     * @return the long
     * @throws Exception the exception
     */
    Long sort(String flag, String key, String dstkey) throws Exception;

    /**
     * Spop string.
     *
     * @param flag the flag
     * @param key  the key
     * @return the string
     * @throws Exception the exception
     */
    String spop(String flag, String key) throws Exception;

    /**
     * Srandmember string.
     *
     * @param flag the flag
     * @param key  the key
     * @return the string
     * @throws Exception the exception
     */
    String srandmember(String flag, String key) throws Exception;

    /**
     * Srandmember list.
     *
     * @param flag  the flag
     * @param key   the key
     * @param count the count
     * @return the list
     * @throws Exception the exception
     */
    List<String> srandmember(String flag, String key, int count) throws Exception;

    /**
     * Strlen long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long strlen(String flag, String key) throws Exception;

    /**
     * Substr string.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the string
     * @throws Exception the exception
     */
    String substr(String flag, String key, int start, int end) throws Exception;

    /**
     * Zadd long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param score  the score
     * @param member the member
     * @return the long
     * @throws Exception the exception
     */
    Long zadd(String flag, String key, double score, String member) throws Exception;

    /**
     * Zadd long.
     *
     * @param flag         the flag
     * @param key          the key
     * @param scoreMembers the score members
     * @return the long
     * @throws Exception the exception
     */
    Long zadd(String flag, String key, Map<String, Double> scoreMembers) throws Exception;

    /**
     * Zcount long.
     *
     * @param flag the flag
     * @param key  the key
     * @param min  the min
     * @param max  the max
     * @return the long
     * @throws Exception the exception
     */
    Long zcount(String flag, final String key, double min, double max) throws Exception;

    /**
     * Zcard long.
     *
     * @param flag the flag
     * @param key  the key
     * @return the long
     * @throws Exception the exception
     */
    Long zcard(String flag, String key) throws Exception;

    /**
     * Zrange set.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the set
     * @throws Exception the exception
     */
    Set<String> zrange(String flag, String key, long start, long end) throws Exception;

    /**
     * Zrank long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param member the member
     * @return the long
     * @throws Exception the exception
     */
    Long zrank(String flag, String key, String member) throws Exception;

    /**
     * Zscore double.
     *
     * @param flag   the flag
     * @param key    the key
     * @param member the member
     * @return the double
     * @throws Exception the exception
     */
    Double zscore(String flag, String key, String member) throws Exception;

    /**
     * Zrange by score set.
     *
     * @param flag the flag
     * @param key  the key
     * @param min  the min
     * @param max  the max
     * @return the set
     * @throws Exception the exception
     */
    Set<String> zrangeByScore(String flag, String key, double min, double max) throws Exception;

    /**
     * Zrange by score set.
     *
     * @param flag   the flag
     * @param key    the key
     * @param min    the min
     * @param max    the max
     * @param offset the offset
     * @param count  the count
     * @return the set
     * @throws Exception the exception
     */
    Set<String> zrangeByScore(String flag, String key, double min, double max, int offset, int count) throws Exception;

    /**
     * Zrange by score set.
     *
     * @param flag the flag
     * @param key  the key
     * @param min  the min
     * @param max  the max
     * @return the set
     * @throws Exception the exception
     */
    Set<String> zrangeByScore(String flag, String key, String min, String max) throws Exception;

    /**
     * Zrange by score set.
     *
     * @param flag   the flag
     * @param key    the key
     * @param min    the min
     * @param max    the max
     * @param offset the offset
     * @param count  the count
     * @return the set
     * @throws Exception the exception
     */
    Set<String> zrangeByScore(String flag, String key, String min, String max, int offset, int count) throws Exception;

    /**
     * Zrevrange with scores set.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the set
     * @throws Exception the exception
     */
    Set<Tuple> zrevrangeWithScores(String flag, String key, long start, long end) throws Exception;

    /**
     * Zrevrank long.
     *
     * @param flag   the flag
     * @param key    the key
     * @param member the member
     * @return the long
     * @throws Exception the exception
     */
    Long zrevrank(String flag, String key, String member) throws Exception;

    /**
     * Zrevrange set.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the set
     * @throws Exception the exception
     */
    Set<String> zrevrange(String flag, String key, long start, long end) throws Exception;

    /**
     * Zrange with scores set.
     *
     * @param flag  the flag
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the set
     * @throws Exception the exception
     */
    Set<Tuple> zrangeWithScores(String flag, String key, long start, long end) throws Exception;

    /**
     * Srem long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param members the members
     * @return the long
     * @throws Exception the exception
     */
    Long srem(String flag, String key, String... members) throws Exception;

    /**
     * Sismember boolean.
     *
     * @param flag   the flag
     * @param key    the key
     * @param member the member
     * @return the boolean
     * @throws Exception the exception
     */
    Boolean sismember(String flag, String key, String member) throws Exception;

    /**
     * Zrem long.
     *
     * @param flag    the flag
     * @param key     the key
     * @param members the members
     * @return the long
     * @throws Exception the exception
     */
    Long zrem(String flag, String key, String... members) throws Exception;

    /**
     * Zincrby double.
     *
     * @param flag      the flag
     * @param key       the key
     * @param increment the increment
     * @param member    the member
     * @return the double
     * @throws Exception the exception
     */
    Double zincrby(String flag, String key, double increment, String member) throws Exception;

    /**
     * Sets and expire.
     *
     * @param flag    the flag
     * @param key     the key
     * @param value   the value
     * @param seconds the seconds
     * @return the and expire
     * @throws Exception the exception
     */
    List<Object> setAndExpire(String flag, String key, String value, int seconds) throws Exception;

    /**
     * Lpush and expire list.
     *
     * @param flag    the flag
     * @param seconds the seconds
     * @param key     the key
     * @param strings the strings
     * @return the list
     * @throws Exception the exception
     */
    List<Object> lpushAndExpire(String flag, int seconds, String key, String... strings) throws Exception;

    /**
     * Rpush and expire list.
     *
     * @param flag    the flag
     * @param seconds the seconds
     * @param key     the key
     * @param strings the strings
     * @return the list
     * @throws Exception the exception
     */
    List<Object> rpushAndExpire(String flag, int seconds, String key, String... strings) throws Exception;

    /**
     * Sadd and expire list.
     *
     * @param flag    the flag
     * @param seconds the seconds
     * @param key     the key
     * @param members the members
     * @return the list
     * @throws Exception the exception
     */
    List<Object> saddAndExpire(String flag, int seconds, String key, String... members) throws Exception;

    /**
     * Zadd and expire list.
     *
     * @param flag         the flag
     * @param key          the key
     * @param scoreMembers the score members
     * @param seconds      the seconds
     * @return the list
     * @throws Exception the exception
     */
    List<Object> zaddAndExpire(String flag, String key, Map<String, Double> scoreMembers, int seconds) throws Exception;

    /**
     * Hmset and expire list.
     *
     * @param flag    the flag
     * @param key     the key
     * @param hash    the hash
     * @param seconds the seconds
     * @return the list
     * @throws Exception the exception
     */
    List<Object> hmsetAndExpire(String flag, String key, Map<String, String> hash, int seconds) throws Exception;
}
