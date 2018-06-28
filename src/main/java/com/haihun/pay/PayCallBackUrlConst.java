package com.haihun.pay;

/**
 * 支付回调常量类
 *
 * @author kaiser·von·d
 * @version 2018/5/2
 */
public class PayCallBackUrlConst {

    private static boolean isDev = true;

    /**
     * 支付宝回调接口
     */
    public static final String ALIPAY_CALL_BACK_URL ;

    /**
     * 微信支付回调接口
     */
    public static final String WECHAT_PAY_CALL_BACK_URL;
//    public static final String WECHAT_PAY_CALL_BACK_URL = "http://17586q9d82.iask.in/pay/weChatPayCallBack";

    /**
     * 银联支付回调接口
     */
    public static final String UNION_PAY_CALL_BACK_URL ;

    static{
        if (isDev) {
            ALIPAY_CALL_BACK_URL = "http://17586q9d82.iask.in/pay/alipayCallBack";
            WECHAT_PAY_CALL_BACK_URL = "http://17586q9d82.iask.in/pay/weChatPayCallBack";
            UNION_PAY_CALL_BACK_URL = "http://17586q9d82.iask.in/pay/testpay";
        } else {
            ALIPAY_CALL_BACK_URL = "http://pay.haihungame.com/pay/alipayCallBack";
            WECHAT_PAY_CALL_BACK_URL = "http://pay.haihungame.com/pay/weChatPayCallBack";
            UNION_PAY_CALL_BACK_URL = "http://pay.haihungame.com/pay/testpay";
        }
    }


}
