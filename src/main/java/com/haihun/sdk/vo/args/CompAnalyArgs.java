package com.haihun.sdk.vo.args;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kaiser·von·d
 * @version 2018/6/5
 */
@Data
@ApiModel(value = "综合分析 参数模型")
public class CompAnalyArgs extends RangeTimePageArgs {

    @ApiModelProperty(value = "应用ID", required = true)
    protected String appId;

    @ApiModelProperty(value = "渠道ID", required = true)
    protected Integer chId;


}
