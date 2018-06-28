package com.haihun.sdk.vo.result;

import com.alibaba.fastjson.JSONObject;
import com.haihun.annotation.ExampleFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kaiser·von·d
 * @version 2018/5/5
 */
@ApiModel(value = "创建订单响 应参数模型", description = OrderResult.PATH)
public class OrderResult extends Result {

    /**
     * swagger2
     * description 声明请规范填写类的包名全路径
     */
    public static final String PATH = "com.haihun.sdk.vo.result.OrderResult";


    public static List<String> alipay = new ArrayList<String>() {{
        add("app_id=xxx");
        add("biz_content={}");
        add("charset=xxx");
        add("format=json");
        add("method=alipay.trade.app.pay");
        add("notify_url=url");
        add("sign_type=RSA2");
        add("timestamp=2016-08-25 20:26:31");
        add("version=1.0");
        add("sign=xxx");

    }};


    public static JSONObject retWechat = new JSONObject() {{
        put("package", "Sign=WXPay");
        put("appid", "xxx");
        put("prepayid", "xxx");
        put("partnerid", "xxx");
        put("noncestr", "uuid");
        put("timestamp", 1234567890);
        put("sign", "str");
        put("orderId", "str");

    }};


    @ExampleFormat
    public static final JSONObject EXAMPLE = new JSONObject() {{
        put("注意", "支付类型不同，返回数据也有所不同");
        put("支付宝", new JSONObject() {{
            put("content", String.join("&", alipay));
        }});
        put("微信", retWechat);
    }};


    /**
     * 数据内容
     */
    @ApiModelProperty(value = "创建订单返回内容")
    protected String data;

    public OrderResult() {
        super();
    }


    public OrderResult(int status, String msg, String data) {
        super(status, msg, data);
        this.data = data;
    }

}
