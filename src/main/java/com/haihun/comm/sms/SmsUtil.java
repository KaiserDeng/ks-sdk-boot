package com.haihun.comm.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信工具类
 *
 * @author kaiser·von·d
 * @version 1.0
 * @date 2018-4-22 下午2:45:29
 */
@Slf4j
public final class SmsUtil {


    /**
     * 短信接口的请求地址
     */
    private final static String SMS_REQUEST_URL = "dysmsapi.aliyuncs.com";


    /**
     * 短信API产品名称（短信产品名固定，无需修改）
     */
    private static final String product = "Dysmsapi";

    /**
     * 应用的key
     */
    private final static String APP_KEY = "LTAIjw8E0X8tHIEM";
    /**
     * 应用的签名
     */
    private final static String APP_SECRET = "lHFKA5ltGWBcST3b3i69RKiYRyBDtA";
    /**
     * 短信签名
     */
    private final static String SMS_FREE_SIGN_NAME = "海魂游戏";
    /**
     * 用户注册模板ID
     */
    public final static String SMS_TEMPLATE_REGISTER = "SMS_134318610";


    /**
     * 找回密码模版ID
     */
    public static final String SMS_TEMPLATE_RESET_PWD = "SMS_134328731";
    /**
     * 快速登录模版ID
     */
    public static final String SMS_TEMPLATE_LOGIN = "SMS_134328729";



    static {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    }

    /**
     * 验证码短信发送方法
     *
     * @param code     验证码
     * @param mobileNo 手机号码
     * @param template 模版ID
     * @return true : 发送成功  false: 发送失败
     */
    public static boolean send(String code, String mobileNo, String template) {
        try {
            log.info(" Start sendSms ...");
            IClientProfile profile = DefaultProfile.getProfile("cn-hanzhou", APP_KEY, APP_SECRET);
            DefaultProfile.addEndpoint("cn-hanzhou", "cn-hanzhou", product, SMS_REQUEST_URL);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();

            //使用post提交
            request.setMethod(MethodType.POST);

            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(mobileNo);

            //必填:短信签名-可在短信控制台中找到
            request.setSignName(SMS_FREE_SIGN_NAME);

            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(template);

            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam("{\"code\":" + code + "}");

            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                log.info("send sms to mobile no " + mobileNo + " succesful ! ");
                return true;

            }
            log.warn("send sms failed ! message : " + SmsErrorCode.valueOf(sendSmsResponse.getCode().replace(".","_").toUpperCase()).getValue());
            return false;
        } catch (Exception e) {
            log.error("send SMS failed ! info: ", e.getMessage());
            return false;
        }
    }
}