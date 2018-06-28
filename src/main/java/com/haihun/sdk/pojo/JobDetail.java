package com.haihun.sdk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 任务抽象业务类
 *
 * @author kaiser·von·d
 * @version 2018-5-28
 */
@Data
@ApiModel(value = "JobDetail")
public class JobDetail {

    @ApiModelProperty
    // job info
    private Job jobDO;

    // trigger info
    private Set<Trigger> triggerDOs;

    // 处理job
    @JsonIgnore
    public transient Consumer<org.quartz.JobDetail> fillWithQuartzJobDetail = jd -> {
        jobDO = new Job();

        // job
        JobKey jk = jd.getKey();

        // name group desc
        BeanUtils.copyProperties(jk, jobDO);
        jobDO.setDescription(jd.getDescription());
        jobDO.setTargetClass(jd.getJobClass().getCanonicalName());

        // ext
        JobDataMap jdm = jd.getJobDataMap();
        if (Objects.nonNull(jdm)) {
            jobDO.setExtInfo(jdm.getWrappedMap());
        }

        this.setJobDO(jobDO);
    };

    // 处理triggers
    @JsonIgnore
    public transient Consumer<List<org.quartz.Trigger>> fillWithQuartzTriggers = trList -> {

        // triggers
        Set<Trigger> tdSet = trList.stream().map(tr -> {
            Trigger td = new Trigger();
            if (tr instanceof CronTrigger) {
                CronTrigger ctr = (CronTrigger) tr;
                td.setCronExpression(ctr.getCronExpression());
            }
            TriggerKey trk = tr.getKey();
            td.setName(trk.getName());
            td.setGroup(trk.getGroup());
            td.setDescription(tr.getDescription());
            return td;
        }).collect(Collectors.toSet());
        this.setTriggerDOs(tdSet);
    };


}
