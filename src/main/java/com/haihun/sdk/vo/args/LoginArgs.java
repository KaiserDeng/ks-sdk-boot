package com.haihun.sdk.vo.args;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author kaiser·von·d
 * @version 2018/5/5
 */
@Data
@ApiModel("用户登录 参数模型")
public class LoginArgs {

    @NotBlank(message = "账号不能为空！")
    @ApiModelProperty(required = true, value = "用户名/手机号")
    private String userName;

    @ApiModelProperty(required = true, value = "密码")
    private String pwd;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(hidden = true)
    // 渠道类型
    private String originType;

    @ApiModelProperty(hidden = true)
    // 渠道类型
    private String userId;

    //设备授权ID
    @NotBlank(message = "duid cannot empty!")
    @ApiModelProperty(required = true,value = "设备授权ID")
    private String duid;

    public String getDuid() {
        return duid;
    }

    public void setDuid(String duid) {
        this.duid = duid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    @NotBlank(message = "游戏ID不能为空！")
    @ApiModelProperty(required = true, value = "请求头携带游戏ID")
    private String appId;


}
