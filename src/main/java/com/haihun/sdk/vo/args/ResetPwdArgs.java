package com.haihun.sdk.vo.args;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author kaiser·von·d
 * @version 2018/5/4
 */
@Data
@ApiModel("忘记/找回密码 参数模型")
public class ResetPwdArgs {

    @NotBlank
    @Length(min = 6,max = 20)
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    @ApiModelProperty(value = "旧密码")
    private String oldPwd;

    @NotBlank
    @Length(min = 6,max = 20)
    @ApiModelProperty(value = "新密码", required = true)
    private String newPwd;

    @ApiModelProperty(value = "验证码")
    private String code;


}
