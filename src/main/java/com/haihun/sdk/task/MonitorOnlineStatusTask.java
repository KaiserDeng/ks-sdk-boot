package com.haihun.sdk.task;

import com.haihun.config.redis.JedisService;
import com.haihun.sdk.service.GameService;
import com.haihun.sdk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 监控用户在线状态任务
 *
 * @author kaiser·von·d
 * @version 2018/5/29
 */
@Component
@Slf4j
public class MonitorOnlineStatusTask implements Job {

    @Autowired
    private JedisService jedisService;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;


    @Override
    public void execute(JobExecutionContext context) {
        try {
            Set<String> gameIds = jedisService.smembers(GameService.GAME_ID_LIST);
            if (gameIds.isEmpty()) {
                List<String> ids = gameService.findAllIds();
                String[] fields = new String[ids.size()];
                ids.toArray(fields);
                // 缓存到redis中,并设置过期时间
                jedisService.sadd(GameService.GAME_ID_LIST, fields);
                jedisService.expire(GameService.GAME_ID_LIST, GameService.GAME_LIST_TIME_OUT);
            }

            gameIds.forEach(appId -> {
                String key = UserService.GAME_STATUS_PREFIX + appId;
                Map<String, String> kv = jedisService.hgetAll(key);
                if (!kv.isEmpty()) {
                    List<String> keys = new ArrayList<>(kv.keySet());
                    keys.forEach(k -> {
                        String token = kv.get(k);
                        // 如果token已经失效，则玩家已经不在线
                        if (StringUtils.isBlank(userService.queryUserByToken(token))) {
                            kv.remove(k);
                        }
                    });
                    if (!kv.isEmpty()) {
                        jedisService.hset(key, kv);
                    } else {
                        jedisService.del(key);
                    }
                }
            });
        } catch (Exception e) {
            log.error("monitor game online mumber failed ! detail : {} ", e.getMessage());
         }
    }
}
