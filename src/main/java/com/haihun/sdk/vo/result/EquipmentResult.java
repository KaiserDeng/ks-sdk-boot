package com.haihun.sdk.vo.result;

import com.alibaba.fastjson.JSONObject;
import com.haihun.annotation.ExampleFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author kaiser·von·d
 * @version 2018/5/5
 */
@ApiModel(value = "设备授权响应 参数模型", description = EquipmentResult.PATH)
public class EquipmentResult extends Result {

    /**
     * swagger2
     * description 声明请规范填写类的包名全路径
     */
    public static final String PATH = "com.haihun.sdk.vo.result.EquipmentResult";

    // 变量名不可以更改

    @ExampleFormat
    public static final JSONObject EXAMPLE = new JSONObject() {{
        put("systemTime", 123456789000L);
        put("duid", "str");
    }};

    /**
     * 数据内容
     */
    @ApiModelProperty(value = "登录返回内容")
    protected String data;


    public EquipmentResult() {
        super();
    }


    public EquipmentResult(int status, String msg, String data) {
        super(status, msg, data);
        this.data = data;
    }


}
