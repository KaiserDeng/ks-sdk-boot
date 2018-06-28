package com.haihun.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ShardedJedisPoolConfig {


    @Autowired
    private RedisPropperties prop;

    @Bean(name = "getShardedJedisPool")
    @Primary
    public ShardedJedisPool getShardedJedisPool() {
        List<JedisShardInfo> shardInfos = getShardInfos();
        if (shardInfos == null) {
            throw new IllegalArgumentException("jedis There are no configuration nodes  !");
        }
        return new ShardedJedisPool(buildJedisPoolConfig(), shardInfos);
    }


    /**
     * 构建Redis配置信息
     *
     * @return Redis配置信息
     */
    @Bean
    @Primary
    public JedisPoolConfig buildJedisPoolConfig() {
        log.info("start read jedisPool configuration ....");
        JedisPoolConfig config = new JedisPoolConfig();
        Map<String, Object> map = prop.getConfiguration();
        if (map != null) {
            int maxTotal = map.get("maxTotal") != null ? (int) map.get("maxTotal") : 8;
            int maxIdle = map.get("maxIdle") != null ? (int) map.get("maxIdle") : 8;
            int maxWait = map.get("maxWait") != null ? (int) map.get("maxWait") : -1;
            boolean testOnBorrow = map.get("testOnBorrow") != null ? (boolean) map.get("testOnBorrow") : false;
            boolean testOnReturn = map.get("testOnReturn") != null ? (boolean) map.get("testOnReturn") : false;
            config.setMaxTotal(maxTotal);
            config.setMaxIdle(maxIdle);
            config.setMaxWaitMillis(maxWait);
            config.setTestOnBorrow(testOnBorrow);
            config.setTestOnReturn(testOnReturn);
        }
        log.info("jedisPool configuration read end ....");
        log.info("jedisPool configuration info : " + config.toString());
        return config;
    }


    /**
     * 读取集群节点
     *
     * @return 节点列表
     */
    @Bean
    @Primary
    public List<JedisShardInfo> getShardInfos() {
        log.info("start read jedisPool cluster nodes configuration ....");
        Map<String, Object> nodes = prop.getNodes();
        if (nodes != null) {
            List<JedisShardInfo> shardInfos = new ArrayList<>();
            JedisShardInfo info = null;
            for (Map.Entry<String, Object> entry : nodes.entrySet()) {
                if (entry.getKey().indexOf("node-") == -1) {
                    throw new IllegalArgumentException("read error not found node format \"node-*\" configruation !");
                }
                Map<String, Object> hostInfo = (Map<String, Object>) entry.getValue();
                info = new JedisShardInfo(((String) hostInfo.get("host")), ((int) hostInfo.get("port")));
                Object pwd = hostInfo.get("password");
                if (pwd != null && !"".equals(pwd)) {
                    info.setPassword((String) pwd);
                }
                shardInfos.add(info);
                log.info("jedisPool cluster node infomation  : " + info.toString());

            }
            return shardInfos;
        }
        return null;
    }
}
