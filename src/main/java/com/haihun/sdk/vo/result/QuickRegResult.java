package com.haihun.sdk.vo.result;

import com.alibaba.fastjson.JSONObject;
import com.haihun.annotation.ExampleFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author kaiser·von·d
 * @version 2018/5/5
 */
@ApiModel(value = "游客登录 响应参数模型", description = QuickRegResult.PATH)
public class QuickRegResult extends Result {

    public static final String PATH = "com.haihun.sdk.vo.result.QuickRegResult";

    @ExampleFormat
    public static final JSONObject EXAMPLE = new JSONObject() {{
        put("userName", "str");
        put("pwd", "str");
        put("userId", "str");
        put("token", "str");
        put("mobile", "str");
    }};
    /**
     * 数据内容
     */
    @ApiModelProperty(value = "登录返回内容")
    protected String data;

    public QuickRegResult() {
        super();
    }

    public QuickRegResult(int status, String msg, String data) {
        super(status, msg, data);
        this.data = data;
    }


}
