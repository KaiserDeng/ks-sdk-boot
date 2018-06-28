package com.haihun.sdk.service;

import com.google.common.collect.Lists;
import com.haihun.sdk.pojo.JobDetail;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时任务操作
 *
 * @author kaiser·von·d
 * @version 2018-5-28
 */
@Service
@Transactional(value = "masterTransactionManager", rollbackFor = {Exception.class, RuntimeException.class})
public class QuartzJobDetailService {

    // SchedulerFactoryBean 创建
    @Autowired
    public Scheduler scheduler;

    // 任务列表
    @Transactional(readOnly = true)
    public List<JobDetail> queryJobList() {
        List<JobDetail> jobDetailDOs = Lists.newArrayList();

        // 数据处理
        Function<Set<JobKey>, List<JobDetail>> copyPropFun = jbst -> {
            List<JobDetail> jddList = jbst.stream().map(jk -> {
                org.quartz.JobDetail jd = null;
//                List<Trigger> trList = this.getTriggerByKey(jk);
                jd = this.getJobDetailByKey(jk);

                // jobDetail
                JobDetail jobDetailDO = new JobDetail();
                jobDetailDO.fillWithQuartzJobDetail.accept(jd);
//                jobDetailDO.fillWithQuartzTriggers.accept(trList);
                return jobDetailDO;
            }).collect(Collectors.toList());
            return jddList;
        };

        try {
            Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            jobDetailDOs = copyPropFun.apply(jobSet);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobDetailDOs;
    }

    /**
     * 查询指定jobkey jobDetail
     *
     * @param jobKey
     * @return
     */
    @Transactional(readOnly = true)
    public JobDetail queryByKey(JobKey jobKey) {
        JobDetail jobDetailDO = new JobDetail();
        org.quartz.JobDetail jobDetail = this.getJobDetailByKey(jobKey);
        if (Objects.nonNull(jobDetail)) {
            List<Trigger> triggerList = this.getTriggerByKey(jobKey);
            jobDetailDO.fillWithQuartzJobDetail.accept(jobDetail);
            jobDetailDO.fillWithQuartzTriggers.accept(triggerList);
        }
        return jobDetailDO;
    }

    /**
     * 添加任务
     *
     * @param jobDetailDO
     */
    public boolean add(JobDetail jobDetailDO) {
        org.quartz.JobDetail jobDetail = jobDetailDO.getJobDO().convert2QuartzJobDetail();
        Set<CronTrigger> triggerSet = jobDetailDO.getTriggerDOs().stream().map(jtd ->
                jtd.convert2QuartzTrigger(jobDetail)
        ).collect(Collectors.toSet());

        // 如果已经存在 则替换
        try {
            scheduler.scheduleJob(jobDetail, triggerSet, true);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除任务
     *
     * @param jobKeyList
     */
    public boolean remove(List<JobKey> jobKeyList) {
        try {
            return scheduler.deleteJobs(jobKeyList);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 停用任务
    public boolean disable(GroupMatcher<JobKey> matcher) {
        try {
            scheduler.pauseJobs(matcher);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 停用所有任务
    public boolean disableAll() {
        try {
            scheduler.pauseAll();
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 启用任务
    public boolean enable(GroupMatcher<JobKey> matcher) {
        try {
            scheduler.resumeJobs(matcher);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 启用所有任务
    public boolean enableAll() {
        try {
            scheduler.resumeAll();
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 立即触发任务
    public boolean triggerNow(JobKey jobKey, JobDataMap jobDataMap) {
        try {
            scheduler.triggerJob(jobKey, jobDataMap);
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据key 获取jobDetail
     *
     * @param jobKey
     * @return
     */
    @Transactional(readOnly = true)
    public org.quartz.JobDetail getJobDetailByKey(JobKey jobKey) {
        org.quartz.JobDetail jd = null;
        try {
            jd = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jd;
    }

    /**
     * 根据key 获取job trigger
     *
     * @param jobKey
     * @return
     */
    public List<Trigger> getTriggerByKey(JobKey jobKey) {
        List<Trigger> triggerList = Lists.newArrayList();
        try {
            triggerList = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return triggerList;
    }
}
