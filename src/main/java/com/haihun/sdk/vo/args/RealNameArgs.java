package com.haihun.sdk.vo.args;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 实名认证参数结构
 *
 * @author kaiser·von·d
 * @version 2018/5/23
 */
@Data
@ApiModel("实名认证 参数模型")
public class RealNameArgs {

    @NotBlank
    @ApiModelProperty(value = "token", required = true)
    private String token;

    @ApiModelProperty(value = "身份证号", required = true)
    @Length(min = 18, max = 18, message = "The length of the id card is fixed to 18 ！")
    private String cardId;

    @ApiModelProperty(value = "真实姓名", required = true)
    @NotBlank(message = "realName cannot Empty !")
    private String realName;

}
