package com.haihun.comm.security;

/**
 * 加密数据bean
 *
 * @author kaiser·von·d
 * @version 2018/5/7
 */
public class EncryptVO {

    /**
     * appId
     */
    private String ak;
    /**
     * 协议payload
     */
    private byte[] body;

    /**
     * 加密摘要
     */
    private String sign;

    /**
     * 原生加密密钥
     */
    private byte[] aesKey;

    /**
     * RSA加密过后的密钥
     */
    private byte[] enKey;

    public byte[] getAesKey() {
        return aesKey;
    }

    public void setAesKey(byte[] aesKey) {
        this.aesKey = aesKey;
    }

    public byte[] getEnKey() {
        return enKey;
    }

    public void setEnKey(byte[] enKey) {
        this.enKey = enKey;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
