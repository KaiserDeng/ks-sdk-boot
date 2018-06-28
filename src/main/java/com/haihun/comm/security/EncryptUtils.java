package com.haihun.comm.security;

import com.haihun.comm.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据加密工具类
 *
 * @author kaiser·von·d
 * @version 2018/5/7
 */
@Slf4j
public class EncryptUtils {


    /**
     * 对数据进行加密并封装返回,
     *
     * @param encryptData 需要加密的数据
     * @param appSecret   应用的secret
     * @param appId       应用的ID
     * @return 封装好的参数
     */
    public static EncryptVO requestEncrypt(String encryptData, String appSecret, String appId) {
        try (ByteArrayOutputStream bais = new ByteArrayOutputStream();
             DataOutputStream dis = new DataOutputStream(bais);) {
            byte[] aesKey = AES.initSecretKey();

            byte[] body = AES.encrypt(encryptData.getBytes("UTF-8"), aesKey);

            byte[] enKey = RSA.encryptByPubAsArr(aesKey, RSA.getPublicKey(Constant.APP_PUBLIC_KEY));


            // 写入 enKey的长度
            dis.writeInt(enKey.length);
            // 写入 enKey的内容
            dis.write(enKey);

            // 写入内容长度
            dis.writeInt(body.length);
            // 写入内容正文
            dis.write(body);
            Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, appSecret.getBytes());
            // 得到加密后的16进制
            String sign = Hex.encodeHexString(mac.doFinal(bais.toByteArray())).toUpperCase();
            // 封装数据
            EncryptVO vo = new EncryptVO();
            vo.setAk(appId);
            vo.setAesKey(aesKey);
            vo.setEnKey(enKey);
            vo.setSign(sign);
            vo.setBody(bais.toByteArray());

            return vo;
        } catch (Exception e) {
            log.error(" encrypt data error! message : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 验证摘要
     *
     * @param appSecret 游戏sercret
     * @param body      解密后的数据
     * @param sign      加密的sign
     * @return 数据是否被篡改
     */
    public static boolean checkSign(String appSecret, byte[] body, String sign) {
        // 根据appsecret 进行加密运算
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, appSecret.getBytes());
        // 得到加密后的16进制
        String deSign = Hex.encodeHexString(mac.doFinal(body)).toUpperCase();
        if (!sign.equalsIgnoreCase(deSign)) {
            throw new RuntimeException("数据被篡改！");
        }
        return true;
    }

    /**
     * 根据输入流进行解密
     */
    public static HashMap<String, byte[]> requestDecrypt(InputStream inputStream) {
        try (DataInputStream dis = new DataInputStream(inputStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)
        ) {
            return requestDecrypt(dis, baos, dos);
        } catch (Exception e) {
            log.error(" Decrypt Message Error Detail : " + e.getMessage());
            return null;
        }
    }

    /**
     * 解码核心方法
     */
    private static HashMap<String, byte[]> requestDecrypt(DataInputStream dis, ByteArrayOutputStream baos, DataOutputStream dos) throws IOException {
        HashMap<String, byte[]> map = new HashMap<>();
        AtomicInteger count = new AtomicInteger(1);
        while (dis.available() > 0 && count.intValue() < 3) {
            // 读取到消息首部的长度
            int headLength = dis.readInt();
            // 记录长度
            dos.writeInt(headLength);
            byte[] arr = new byte[headLength];

            dis.read(arr, 0, headLength);
            // 记录 payload
            dos.write(arr);
            if (count.intValue() == 1) {
                map.put("aesKey", arr);
            } else if (count.intValue() == 2) {
                map.put("payload", arr);
            }
            count.incrementAndGet();
        }
        map.put("body", baos.toByteArray());
        if (map.isEmpty()) {
            return null;
        }
        return map;
    }


    /**
     * 响应数据加密
     *
     * @param encryptData 需要加密的数据
     * @param appSecret   应用的secret
     * @param appId       应用的ID
     * @return 封装好的参数
     */
    public static EncryptVO responseEncrypt(String encryptData, String appSecret, String appId, byte[] aesKey) {
        try (ByteArrayOutputStream bais = new ByteArrayOutputStream();
             DataOutputStream dis = new DataOutputStream(bais);) {

            byte[] body = AES.encrypt(encryptData.getBytes(), aesKey);

            byte[] enKey = RSA.encryptByPubAsArr(aesKey, RSA.getPublicKey(Constant.APP_PUBLIC_KEY));

            // 写入 enKey的长度
            dis.writeInt(enKey.length);
            // 写入 enKey的内容
            dis.write(enKey);

            // 写入内容长度
            dis.writeInt(body.length);
            // 写入内容正文
            dis.write(body);
            Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, appSecret.getBytes());
            // 得到加密后的16进制
            String sign = Hex.encodeHexString(mac.doFinal(body)).toUpperCase();
            // 封装数据
            EncryptVO vo = new EncryptVO();
            vo.setAk(appId);
            vo.setSign(sign);
            vo.setBody(body);

            return vo;
        } catch (Exception e) {
            log.error(" encrypt data error! message : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 响应数据解密
     *
     * @param responseData 响应数据
     * @param aesKey       aesKey
     * @return 响应原文
     */
    public static String responseDecrypt(byte[] responseData, byte[] aesKey) {
        try {
            return new String(AES.decrypt(responseData, aesKey), "UTF-8");
        } catch (Exception e) {
            log.error(" Decrypt Message Error Detail : " + e.getMessage());
            return null;
        }
    }


}
