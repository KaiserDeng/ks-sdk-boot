package com.haihun.pay;

import static com.haihun.pay.PayCallBackUrlConst.*;

/**
 * 支付类型枚举类
 *
 * @author kaiser·von·d
 * @version 2018/4/27
 */

public enum PayTypeEnum {

    AliPay("0", ALIPAY_CALL_BACK_URL, "支付宝"),
    Wechatpay("1", WECHAT_PAY_CALL_BACK_URL, "微信"),
    UnionPay("2", UNION_PAY_CALL_BACK_URL, "银联");

    PayTypeEnum(String key, String value, String name) {
        this.key = key;
        this.value = value;
        this.name = name;
    }

    private String key;
    private String name;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
