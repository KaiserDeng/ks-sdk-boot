package com.haihun.sdk.task;

import com.alibaba.fastjson.JSONObject;
import com.haihun.config.redis.JedisService;
import com.haihun.sdk.service.GameOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知游戏服务器 任务类
 *
 * @author kaiser·von·d
 * @version 2018/5/29
 */
@Component
@Slf4j
public class NotifyGameServerTask implements Job {

    @Autowired
    private JedisService jedisService;

    @Autowired
    private GameOrderService gameOrderService;

    // 重试次数，与间隔时间
    Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
        put(2, 240000);   // 第一次
        put(3, get(2) + 600000);
        put(4, get(3) + 600000);
        put(5, get(4) + 3600000);
        put(6, get(5) + 7200000);
        put(7, get(6) + 21600000);
        put(8, get(7) + 54000000);
    }};

    /**
     * 如果游戏服务器返回的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
     * 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 从队列中获取到重试任务
        String taskJson = null;
        try {
            taskJson = jedisService.rPop(GameOrderService.ORDER_RETRY_NOTIFY_QUEUE);
            if (StringUtils.isNotBlank(taskJson)) {
                JSONObject task = JSONObject.parseObject(taskJson);
                Long lastSendTime = task.getLong(GameOrderService.LAST_SEND_TIME);
                String notifyUrl = task.getString(GameOrderService.NOTIFY_URL);
                Integer sendCount = task.getInteger(GameOrderService.RETRY_NOTIFY_COUNT) + 1;

                if (sendCount <= Collections.max(map.keySet())) {
                    // 重试发送
                    if (System.currentTimeMillis() - lastSendTime >= map.get(sendCount)) {
                        gameOrderService.notifyToGame(task, notifyUrl, sendCount);
                    } else {
                        jedisService.lPush(GameOrderService.ORDER_RETRY_NOTIFY_QUEUE, taskJson);
                    }
                }
            }
        } catch (Exception e) {
            log.error("retry send message to game server failed ! detail : {} ", e.getMessage());
            // 如果出现异常，将数据重新加入到重试队列
            if (StringUtils.isNotBlank(taskJson)) {
                jedisService.lPush(GameOrderService.ORDER_RETRY_NOTIFY_QUEUE, taskJson);
            }
        }
    }
}
