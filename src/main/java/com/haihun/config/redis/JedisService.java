package com.haihun.config.redis;

import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Redis工具类
 *
 * @author kaiser·von·d
 * @version 2018/4/25
 */
@Component
public class JedisService {

    @Resource(name = "getShardedJedisPool")
    private ShardedJedisPool shardedJedisPool;

    private <R> R execute(Function<ShardedJedis, R> fun) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            return fun.apply(jedis);
        } finally {
            if (jedis != null) {
                // 释放资源
                jedis.close();
            }
        }
    }

    /**
     * 设置一个redis字符串值
     *
     * @param key   键
     * @param value 值
     * @return 设置结果
     */
    public String set(final String key, final String value) {
        return execute(jedis -> jedis.set(key, value));
    }

    /**
     * 设置一个redis字符串值并设置其生存时间
     *
     * @param key           键
     * @param expireSeconds 生存时间(秒)
     * @param value         值
     * @return 设置结果
     */
    public String setex(final String key, final int expireSeconds, final String value) {
        return execute(jedis -> jedis.setex(key, expireSeconds, value));
    }

    /**
     * 获取key对应的值
     *
     * @param key 键
     * @return 值
     */
    public String get(final String key) {
        return execute(jedis -> jedis.get(key));
    }

    /**
     * 将一个存在键设置其生存时间
     *
     * @param key           键
     * @param expireSeconds 时间(秒）
     * @return
     */
    public Long expire(final String key, final int expireSeconds) {
        return execute(jedis -> jedis.expire(key, expireSeconds));
    }

    /**
     * 删除key对应的值
     *
     * @param key 键
     * @return 设置结果
     */
    public Long del(final String key) {
        return execute(jedis -> jedis.del(key));
    }

    /**
     * 从列表右边弹出一个元素
     *
     * @param key 键
     * @return 弹出元素
     */
    public String rPop(final String key) {
        return execute(jedis -> jedis.rpop(key));
    }

    /**
     * 从列表左边弹出一个元素
     *
     * @param key 键
     * @return 弹出元素
     */
    public String lPop(final String key) {
        return execute(jedis -> jedis.lpop(key));
    }

    /**
     * 向队列中push一或多个元素
     *
     * @param key   键
     * @param value 值
     * @return 成功1，失败0
     */
    public Long lPush(final String key, final String... value) {
        return execute(jedis -> jedis.lpush(key, value));
    }

    /**
     * 从列表中获取片段元素,但是不删除元素
     *
     * @param key   键
     * @param start 索引值
     * @param end   索引值如果输入-1表示是最大长度
     * @return 结果集
     */
    public List<String> lRange(final String key, final Long start, final Long end) {
        return execute(jedis -> jedis.lrange(key, start, end));
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 结果集
     */
    public Long lLength(final String key) {
        return execute(jedis -> jedis.llen(key));
    }

    /**
     * 获取列表指定位置的元素
     *
     * @param key   键
     * @param index 索引
     * @return 元素
     */
    public String lIndex(final String key, final Long index) {
        return execute(jedis -> jedis.lindex(key, index));
    }

    /**
     * 创建或向一个散列，并添加域值
     *
     * @param key 键
     * @param map 域值对
     * @return 返回旧域值
     */
    public String hmset(final String key, final Map<String, String> map) {
        return execute(jedis -> jedis.hmset(key, map));
    }

    /**
     * 从一个散列中获取一或多个域值
     *
     * @param key    键
     * @param fields 一或多个域
     * @return 值列表
     */
    public List<String> hmget(final String key, final String... fields) {
        return execute(jedis -> jedis.hmget(key, fields));
    }

    /**
     * 获取指定key的所有域值
     *
     * @param key 键
     * @return 所有的域值对
     */
    public Map<String, String> hgetAll(final String key) {
        return execute(jedis -> jedis.hgetAll(key));
    }

    /**
     * 从一个散列中删除一或多个域
     *
     * @param key    键
     * @param fields 需要删除的域
     * @return 成功1，失败0
     */
    public Long hdel(final String key, final String... fields) {
        return execute(jedis -> jedis.hdel(key, fields));
    }

    /**
     * 创建或对一个散列并设置域值
     *
     * @param key   键
     * @param field 域
     * @param value 值
     * @return 1. 成功 0. 失败
     */
    public Long hset(final String key, final String field, final String value) {
        return execute(jedis -> jedis.hset(key, field, value));
    }

    /**
     * 向一个散列批量插入键值
     *
     * @param key  键
     * @param pair field-value
     * @return 是否插入成功
     */
    public Boolean hset(final String key, final Map<String, String> pair) {
        AtomicBoolean flag = new AtomicBoolean(false);
        pair.forEach((k, v) -> {
            hset(key, k, v);
            flag.set(true);
        });
        return flag.get();
    }

    /**
     * 创建或对一个散列设置域值，如果存在该域则返回 0
     *
     * @param key   键
     * @param field 域
     * @param value 值
     * @return
     */
    public Long hsetnx(final String key, final String field, final String value) {
        return execute(jedis -> jedis.hsetnx(key, field, value));
    }

    /**
     * 获取指定key和指定域中的值
     *
     * @param key   key
     * @param field 域
     * @return 值
     */
    public String hget(final String key, final String field) {
        return execute(jedis -> jedis.hget(key, field));
    }

    /**
     * 如果不存在则set
     *
     * @param key   键
     * @param value 值
     * @return 成功1 失败0
     */
    public Long setnx(final String key, final String value) {
        return execute(jedis -> jedis.setnx(key, value));
    }

    /**
     * 获取key剩余有新时间
     *
     * @param key 键
     * @return 有效时长
     */
    public Long tll(final String key) {
        return execute(jedis -> jedis.ttl(key));
    }


    /**
     * 阻塞方法，根据key获取redis队列元素
     *
     * @param key 键
     */
    public List<String> brop(final String key) {
        return execute(jedis -> jedis.brpop(1000, key));
    }


    /**
     * 创建或对一个无序列表设置值
     *
     * @param key 键
     */
    public Long sadd(final String key, final String... fields) {
        return execute(jedis -> jedis.sadd(key, fields));
    }

    /**
     * 获取无序列表中所有的值
     *
     * @param key 键
     * @return 值集合
     */
    public Set<String> smembers(final String key) {
        return execute(jedis -> jedis.smembers(key));
    }

    /**
     * 判断无序集合中是否存在指定的值
     *
     * @param key    键
     * @param member 需要判断的值
     * @return 是否存在
     */
    public Boolean sismember(final String key, final String member) {
        return execute(jedis -> jedis.sismember(key, member));
    }

}
