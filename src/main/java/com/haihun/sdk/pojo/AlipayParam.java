package com.haihun.sdk.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 支付宝回调(可选)参数
 *
 * @author kaiser·von·d
 * @version 2018/4/28
 */
@Data
public class AlipayParam {

    // 以下为必传参数

    /**
     * 通知时间<br/>
     * 是否必填： 是<br/>
     * 描述 ：通知的发送时间。格式为yyyy-MM-dd HH:mm:ss<br/>
     * 范例：　通知的发送时间。格式为yyyy-MM-dd HH:mm:ss
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date notify_time;
    /**
     * 通知类型<br/>
     * 是否必填： 是<br/>
     * 描述 ：通知的类型<br/>
     * 范例：　trade_status_sync
     */

    protected String notify_type;
    /**
     * 通知校验ID<br/>
     * 是否必填：是<br/>
     * 描述 ：通知校验ID<br/>
     * 范例：　ac05099524730693a8b330c5ecf72da9786
     */

    protected String notify_id;
    /**
     * 支付宝分配给开发者的应用Id  <br/>
     * 是否必填： 是  <br/>
     * 描述 ：支付宝分配给开发者的应用Id     <br/>
     * 范例：　2014072300007148
     */
    protected String app_id;
    /**
     * 编码格式<br/>
     * 是否必填： 是<br/>
     * 描述 ：编码格式，如utf-8、gbk、gb2312等<br/>
     * 范例：　编码格式，如utf-8、gbk、gb2312等
     */
    protected String charset;
    /**
     * 接口版本<br/>
     * 是否必填： 是<br/>
     * 描述 ：调用的接口版本，固定为：1.0<br/>
     * 范例：　1.0
     */
    protected String version;
    /**
     * 签名类型<br/>
     * 是否必填： 是<br/>
     * 描述 ：商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2<br/>
     * 范例：　RSA2
     */

    protected String sign_type;
    /**
     * 签名<br/>
     * 是否必填： 是<br/>
     * 描述 ：<a href='https://docs.open.alipay.com/204/105301/'>请参考异步返回结果的验签<a/><br/>
     * 范例：　601510b7970e52cc63db0f44997cf70e
     */

    protected String sign;
    /**
     * 支付宝交易号 <br/>
     * 是否必填：  是<br/>
     * 描述 ：支付宝交易凭证号      <br/>
     * 范例：　2013112011001004330000121536
     */
    protected String trade_no;
    /**
     * 商户订单号<br/>
     * 是否必填： 是<br/>
     * 描述 ：原支付请求的商户订单号<br/>
     * 范例：　6823789339978248
     */

    protected String out_trade_no;

    /**
     * 商户业务号<br/>
     * 是否必填： 否<br/>
     * 描述 ：商户业务ID，主要是退款通知中返回退款申请的流水号<br/>
     * 范例：　商户业务ID，主要是退款通知中返回退款申请的流水号
     */
    protected String out_biz_no;
    /**
     * 买家支付宝用户号<br/>
     * 是否必填： 否<br/>
     * 描述 ：买家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字<br/>
     * 范例：　买家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字
     */
    protected String buyer_id;


    /**
     * 买家支付宝账号<br/>
     * 是否必填： 否<br/>
     * 描述 ：买家支付宝账号<br/>
     * 范例：　买家支付宝账号
     */
    private String buyer_logon_id;
    /**
     * 卖家支付宝用户号<br/>
     * 是否必填： 否 <br/>
     * 描述 ：卖家支付宝用户号   <br/>
     * 范例：　卖家支付宝用户号
     */
    private String seller_id;
    /**
     * 卖家支付宝账号<br/>
     * 是否必填： 否<br/>
     * 描述 ：卖家支付宝账号<br/>
     * 范例：　卖家支付宝账号
     */
    private String seller_email;
    /**
     * 交易状态  <br/>
     * 是否必填： 否<br/>
     * 描述 ：交易目前所处的状态，见交易状态说明　　　<br/>
     * 范例：　交易目前所处的状态，见交易状态说明     <br/>
     */
    private String trade_status;
    /**
     * 订单金额<br/>
     * 是否必填： 否<br/>
     * 描述 ：本次交易支付的订单金额，单位为人民币（元）<br/>
     * 范例：　本次交易支付的订单金额，单位为人民币（元）
     */
    private Number total_amount;
    /**
     * 实收金额<br/>
     * 是否必填： 否<br/>
     * 描述 ：商家在交易中实际收到的款项，单位为元<br/>
     * 范例：　商家在交易中实际收到的款项，单位为元
     */
    private Number receipt_amount;
    /**
     * 开票金额<br/>
     * 是否必填： 否<br/>
     * 描述 ：用户在交易中支付的可开发票的金额<br/>
     * 范例：　用户在交易中支付的可开发票的金额
     */
    private Number invoice_amount;
    /**
     * 付款金额<br/>
     * 是否必填： 否<br/>
     * 描述 ：用户在交易中支付的金额<br/>
     * 范例：　用户在交易中支付的金额
     */
    private Number buyer_pay_amount;
    /**
     * 集分宝金额<br/>
     * 是否必填： 否<br/>
     * 描述 ：使用集分宝支付的金额<br/>
     * 范例：　使用集分宝支付的金额
     */
    private Number point_amount;
    /**
     * 总退款金额<br/>
     * 是否必填： 否<br/>
     * 描述 ：退款通知中，返回总退款金额，单位为元，支持两位小数<br/>
     * 范例：　退款通知中，返回总退款金额，单位为元，支持两位小数
     */
    private Number refund_fee;
    /**
     * 订单标题<br/>
     * 是否必填： 否<br/>
     * 描述 ：商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来<br/>
     * 范例：　商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来
     */
    private String subject;
    /**
     * 商品描述<br/>
     * 是否必填： 否<br/>
     * 描述 ：该订单的备注、描述、明细等。对应请求时的body参数，原样通知回来<br/>
     * 范例：　该订单的备注、描述、明细等。对应请求时的body参数，原样通知回来
     */
    private String body;
    /**
     * 交易创建时间<br/>
     * 是否必填： 否<br/>
     * 描述 ：该笔交易创建的时间。格式为yyyy-MM-dd HH:mm:ss<br/>
     * 范例：　该笔交易创建的时间。格式为yyyy-MM-dd HH:mm:ss
     */

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_create;
    /**
     * 交易付款时间<br/>
     * 是否必填： 否<br/>
     * 描述 ：该笔交易的买家付款时间。格式为yyyy-MM-dd HH:mm:ss<br/>
     * 范例：　该笔交易的买家付款时间。格式为yyyy-MM-dd HH:mm:ss
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_payment;
    /**
     * 交易退款时间<br/>
     * 是否必填： 否<br/>
     * 描述 ：该笔交易的退款时间。格式为yyyy-MM-dd HH:mm:ss.S<br/>
     * 范例：　该笔交易的退款时间。格式为yyyy-MM-dd HH:mm:ss.S
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_refund;
    /**
     * 交易结束时间      <br/>
     * 是否必填： 否 <br/>
     * 描述 ：该笔交易结束时间。格式为yyyy-MM-dd HH:mm:ss  <br/>
     * 范例：　该笔交易结束时间。格式为yyyy-MM-dd HH:mm:ss
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_close;
    /**
     * 支付金额信息 <br/>
     * 是否必填： 否    <br/>
     * 描述 ：支付成功的各个渠道金额信息，详见资金明细信息说明     <br/>
     * 范例：　支付成功的各个渠道金额信息，详见资金明细信息说明
     */
    private String fund_bill_list;
    /**
     * 回传参数     <br/>
     * 是否必填： 否   <br/>
     * 描述 ：公共回传参数，如果请求时传递了该参数，则返回给商户时会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝  <br/>
     * 范例：　公共回传参数，如果请求时传递了该参数，则返回给商户时会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝
     */
    private String passback_params;
    /**
     * 优惠券信息 <br/>
     * 是否必填： 否  <br/>
     * 描述 ：本交易支付时所使用的所有优惠券信息，详见优惠券信息说明   <br/>
     * 范例：　本交易支付时所使用的所有优惠券信息，详见优惠券信息说明
     */
    private String voucher_detail_list;



    @Override
    public String toString() {
        return "AlipayParam{" +
                "out_biz_no='" + out_biz_no + '\'' +
                ", buyer_id='" + buyer_id + '\'' +
                ", buyer_logon_id='" + buyer_logon_id + '\'' +
                ", seller_id='" + seller_id + '\'' +
                ", seller_email='" + seller_email + '\'' +
                ", trade_status='" + trade_status + '\'' +
                ", total_amount=" + total_amount +
                ", receipt_amount=" + receipt_amount +
                ", invoice_amount=" + invoice_amount +
                ", buyer_pay_amount=" + buyer_pay_amount +
                ", point_amount=" + point_amount +
                ", refund_fee=" + refund_fee +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", gmt_create=" + gmt_create +
                ", gmt_payment=" + gmt_payment +
                ", gmt_refund=" + gmt_refund +
                ", gmt_close=" + gmt_close +
                ", fund_bill_list='" + fund_bill_list + '\'' +
                ", passback_params='" + passback_params + '\'' +
                ", voucher_detail_list='" + voucher_detail_list + '\'' +
                ", notify_time=" + notify_time +
                ", notify_type='" + notify_type + '\'' +
                ", notify_id='" + notify_id + '\'' +
                ", app_id='" + app_id + '\'' +
                ", charset='" + charset + '\'' +
                ", version='" + version + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", sign='" + sign + '\'' +
                ", trade_no='" + trade_no + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                '}';
    }
}
