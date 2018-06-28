package com.haihun.pay.alipay;

/**
 * @author kaiser·von·d
 * @version 2018/5/2
 */
public class AlipayUrlConst {
    /**
     * App支付接口
     */
    public static final String ALIPAY_TRADE_APP_PAY = "alipay.trade.app.pay";
    /**
     * 交易关闭接口
     */
    public static final String ALIPAY_TRADE_CLOSE = "alipay.trade.close";
    /**
     * 交易状态查询接口<br/>
     * 通过此接口查询某笔交易的状态，交易状态：<br/>
     * 交易创建，等待买家付款；未付款交易超时关闭，或支付完成后全额退款；<br/>
     * 交易支付成功；交易结束，不可退款。
     */
    public static final String ALIPAY_TRADE_QUERY = "alipay.trade.query";
    /**
     * "交易退款接口
     */
    public static final String ALIPAY_TRADE_REFUND = "https://alipay.trade.refund";
    /**
     * 退款查询接口
     */
    public static final String ALIPAY_TRADE_FASTPAY_REFUND_QUERY = "https://alipay.trade.fastpay.refund.query";
    /**
     * "账单查询接口
     */
    public static final String ALIPAY_DATA_DATASERVICE_BILL_DOWNLOADURL_QUERY = "https://alipay.data.dataservice.bill.downloadurl.query";

    /**
     * 阿里支付网关
     */
    public static final String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do";


}
