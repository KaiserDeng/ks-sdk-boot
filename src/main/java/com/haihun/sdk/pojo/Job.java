package com.haihun.sdk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.core.jmx.JobDataMapSupport;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 作业DO
 *
 * @author kaiser·von·d
 * @version 2018-5-28
 */
@Data
@Slf4j
public class Job {

    private static final Map<String, Class<? extends org.quartz.Job>> SUPPORTED_JOB_TYPES = new HashMap<String, Class<? extends org.quartz.Job>>() ;


    // job info
    @ApiModelProperty(value = "任务名称")
    private String name;
    @ApiModelProperty(value = "任务组")
    private String group;
    @ApiModelProperty(value = "任务执行类")
    private String targetClass;
    @ApiModelProperty(value = "任务描述")
    private String description;

    // ext info
    // supportExtFields
    @ApiModelProperty(value = "拓展字段", dataType = "Map[String,Object]")
    private Map<String, Object> extInfo;

    public JobDetail convert2QuartzJobDetail() {
        Class<? extends org.quartz.Job> clazz = null;

        // 如果未定义 则根据extInfo里type获取默认处理类
        if (Objects.isNull(this.targetClass)) {
            String type = String.valueOf(this.extInfo.get("type"));
            clazz = SUPPORTED_JOB_TYPES.get(type);
            checkNotNull(clazz, "未找到匹配type的Job");
            this.targetClass = clazz.getCanonicalName();
        }
        try {
            clazz = (Class<org.quartz.Job>) ClassUtils.resolveClassName(this.targetClass, this.getClass().getClassLoader());
        } catch (IllegalArgumentException e) {
            log.error("加载类错误", e);
        }

        return JobBuilder.newJob()
                .ofType(clazz)
                .withIdentity(this.name, this.getGroup())
                .withDescription(this.description)
                .setJobData(JobDataMapSupport.newJobDataMap(this.extInfo))
                .build();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JobDO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", targetClass='").append(targetClass).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", extInfo=").append(extInfo);
        sb.append('}');
        return sb.toString();
    }

    public static final String HTTP_JOB = "http_job";
    public static final String THRIFT_JOB = "thrift_job";

}
