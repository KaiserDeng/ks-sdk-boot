package com.haihun.comm.security;

import java.security.MessageDigest;

/**
 * MD5加密
 *
 * @author kaiser·von·d
 * @version 2018/4/23
 */
public final class MD5 {

    /**
     * 获取MD5加密过后的字符串
     *
     * @param str 明文
     * @return MD5加密过后的字符串32位
     */
    public static String getMD5(String str) {
        String res = "";
        try {
            /** 获取MD5加密对象 */
            MessageDigest md = MessageDigest.getInstance("MD5");
            /** 调用加密方法 */
            md.update(str.getBytes("utf-8"));
            /** 获取加密后的字节数组(16位) */
            byte[] md5Bytes = md.digest();
            /** 把加密后的16位字位的字节数组转化成32字符串, 把其中一位转化成16进制的两位
             * 如果转化成16进制不够两位前面补零
             */
            for (int i = 0; i < md5Bytes.length; i++) {
                int temp = md5Bytes[i] & 0xFF;
                if (temp <= 0xF) {
                    res += "0";
                }
                res += Integer.toHexString(temp);
            }
        } catch (Exception e) {
        }
        return res;
    }
}