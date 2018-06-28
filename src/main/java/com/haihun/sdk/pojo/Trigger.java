package com.haihun.sdk.pojo;

import com.google.common.base.Strings;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.quartz.*;

import java.text.ParseException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 触发器域
 *
 * @author kaiser·von·d
 * @version 2018-5-28
 */
@Data
@ApiModel(value = "触发器 参数模型")
public class Trigger {

    // trigger info
    @ApiModelProperty(value = "触发器名称")
    private String name;
    @ApiModelProperty(value = "触发器组")
    private String group;
    @ApiModelProperty(value = "cron表达式")
    private String cronExpression;
    @ApiModelProperty(value = "触发器描述")
    private String description;

    public CronTrigger convert2QuartzTrigger(org.quartz.JobDetail jobDetail) {
        CronExpression ce = null;
        try {
            checkArgument(!Strings.isNullOrEmpty(cronExpression), "cronExpression参数非法");
            ce = new CronExpression(this.cronExpression);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(ce))
                .withIdentity(this.name, this.group)
                .withDescription(this.description)
                .build();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TriggerDO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
