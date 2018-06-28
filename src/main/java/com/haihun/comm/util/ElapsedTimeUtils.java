package com.haihun.comm.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 线程安全的方法耗时计算工具
 *
 * @author kaiser·von·d
 * @version  2018-5-28
 */
@Slf4j
public class ElapsedTimeUtils {

    // 每个线程持有一个timeHolder
    // 保证time timeEnd 方法成对出现
    private static final ThreadLocal<TimeHolder> threadTimeHolder = new ThreadLocal<>();

    private static class TimeHolder {
        private Map<String,Long> keyElapsedTime;
        public TimeHolder(){
            keyElapsedTime = Maps.newHashMap();
        }

        public boolean canRemoved(){
            return Objects.isNull(keyElapsedTime) || keyElapsedTime.isEmpty();
        }

        public void start(String key){

            // 同一线程内 相同key 直接覆盖
            keyElapsedTime.put(key,System.nanoTime());
        }

        public long elapse(String key){
            long endTime = System.nanoTime();
            long startTime = keyElapsedTime.getOrDefault(key,System.nanoTime());
            return endTime - startTime;
        }
    }

    public static void time(String key){
        TimeHolder timeHolder = threadTimeHolder.get();
        if (Objects.isNull(timeHolder)) {
            timeHolder = new TimeHolder();
            threadTimeHolder.set(timeHolder);
        }
        timeHolder.start(key);
    }

    public static void timeEnd(String key){
        TimeHolder timeHolder = threadTimeHolder.get();
        long time = timeHolder.elapse(key);
        if (timeHolder.canRemoved()) {
            threadTimeHolder.remove();
        }
        long threadId = Thread.currentThread().getId();
        log.info("thread: {} | key: {} | {}ms",threadId, key, TimeUnit.NANOSECONDS.toMillis(time));
    }
}
