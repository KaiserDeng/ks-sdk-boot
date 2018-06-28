package com.haihun.sdk.vo.args;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kaiser·von·d
 * @version 2018/6/5
 */
@Data
@ApiModel(value = "分页+时间区间 参数模型")
public class RangeTimePageArgs {

    @ApiModelProperty(value = "页号", required = true)
    protected Integer page = 1;

    @ApiModelProperty(value = "页大小", required = true)
    protected Integer limit = 20;

    @ApiModelProperty(value = "开始时间", required = true)
    protected String beginTime;

    @ApiModelProperty(value = "结束时间", required = true)
    protected String endTime;

}
