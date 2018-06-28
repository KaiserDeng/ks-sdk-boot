package com.haihun.sdk.vo.args;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author kaiser·von·d
 * @version 2018/6/8
 */
@Data
@ApiModel("绑定手机 参数模型")
public class BindMobileArgs  {


    @ApiModelProperty(value = "手机号",required = true)
    @NotEmpty
    private String mobile;

    @NotEmpty
    @ApiModelProperty(value = "token",required = true)
    private String token;

    @NotEmpty
    @ApiModelProperty(value = "密码",required = true)
    private String pwd;

    @NotEmpty
    @ApiModelProperty(value = "验证码",required = true)
    private String smsVerifyCode;

}
