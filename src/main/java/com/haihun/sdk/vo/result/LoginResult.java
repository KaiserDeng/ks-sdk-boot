package com.haihun.sdk.vo.result;

import com.alibaba.fastjson.JSONObject;
import com.haihun.annotation.ExampleFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author kaiser·von·d
 * @version 2018/5/5
 */
@ApiModel(value = "登录响应 参数模型", description = LoginResult.PATH)
public class LoginResult extends Result {

    /**
     * swagger2
     * description 声明请规范填写类的包名全路径
     */
    public static final String PATH = "com.haihun.sdk.vo.result.LoginResult";

    @ExampleFormat
    public static final JSONObject EXAMPLE = new JSONObject() {{
        put("userName", "str");
        put("token", "str");
        put("userId", "hh123456");
        put("expire", 3600);
        put("mobile", "13888888888");
    }};

    /**
     * 数据内容
     */
    @ApiModelProperty(value = "登录返回内容")
    protected String data;


    public LoginResult() {
        super();
    }


    public LoginResult(int status, String msg, String data) {
        super(status, msg, data);
        this.data = data;
    }


}
