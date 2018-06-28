package com.haihun.pay.alipay;

/**
 * 阿里支付枚举类
 *
 * @author kaiser·von·d
 * @version 2018/4/28
 */
public enum AlipayEnum {

    WAIT_BUYER_PAY(1, "交易创建，等待买家付款"),
    TRADE_CLOSED(2, "未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_SUCCESS(3, "交易支付成功"),
    TRADE_FINISHED(4, "交易结束，不可退款");

    private Integer key;
    private String value;

    AlipayEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
