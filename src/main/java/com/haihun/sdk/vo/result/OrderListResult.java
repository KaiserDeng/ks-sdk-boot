package com.haihun.sdk.vo.result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haihun.annotation.ExampleFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author kaiser·von·d
 * @version 2018/6/4
 */
@ApiModel(value = "订单列表 参数模型", description = OrderListResult.PATH)
public class OrderListResult extends Result {

    /**
     * swagger2
     * description 声明请规范填写类的包名全路径
     */
    public static final String PATH = "com.haihun.sdk.vo.result.OrderListResult";

    @ExampleFormat
    public static final JSONArray EXAMPLE = new JSONArray() {{
        add(new JSONObject() {{
            put("createTime", "2018-05-23 18:18:18");
            put("paymentType", "支付宝，微信，银联");
            put("status", "0. 否 1. 是");
            put("title", "50钻石");
            put("totalFee", "0.01(元)");
        }});
    }};

    /**
     * 数据内容
     */
    @ApiModelProperty(value = "登录返回内容")
    protected String data;


    public OrderListResult() {
        super();
    }


    public OrderListResult(int status, String msg, String data) {
        super(status, msg, data);
        this.data = data;
    }


}
