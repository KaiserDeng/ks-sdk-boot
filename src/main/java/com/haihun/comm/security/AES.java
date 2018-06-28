package com.haihun.comm.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * AES Coder<br/>
 * secret key length:   128bit, default:    128 bit<br/>
 * mode:    ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128<br/>
 * padding: Nopadding/PKCS5Padding/ISO10126Padding/
 *
 * @author kaiser·von·d
 * @version 2018/4/28
 */
public class AES {

    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    public static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private AES() {
    }

    /**
     * 初始化密钥二进制密钥
     *
     * @return byte[] 密钥
     * @throws Exception
     */
    public static byte[] initSecretKey() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小
        //AES 要求密钥长度为 128
        kg.init(128);
        //生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 初始化Base64密钥
     *
     * @return
     */
    public static String initBase64SecretKey() {
        return Base64.encodeBase64String(initSecretKey());
    }

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     * @return 密钥
     */
    private static Key convertKey(byte[] key) {
        //生成密钥
        return getSecretKeySpec(key);
    }

    /**
     * 转换密钥
     *
     * @param base64Key base64密钥
     * @return 密钥
     */
    private static Key convertKey(String base64Key) {
        //生成密钥
        return convertKey(Base64.decodeBase64(base64Key));
    }

    /**
     * 生成密钥
     *
     * @param key 二进制密钥
     * @return
     */
    private static Key getSecretKeySpec(byte[] key) {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 将加密数据
     *
     * @param data      待加密数据
     * @param base64Key base64密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static String encrypt(String data, String base64Key) throws Exception {
        return Base64.encodeBase64String(encrypt(data.getBytes(), convertKey(base64Key)));
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  二进制密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }


    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        return encrypt(data, convertKey(key), cipherAlgorithm);
    }

    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }


    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  二进制密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param base64Data 待解密的Base64数据
     * @param Base64Key  Base64密钥
     * @return String   解密数据
     * @throws Exception
     */
    public static String decrypt(String base64Data, String Base64Key) throws Exception {
        return new String(decrypt(Base64.decodeBase64(base64Data), convertKey(Base64Key), DEFAULT_CIPHER_ALGORITHM), "UTF-8");
    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        return decrypt(data, convertKey(key), cipherAlgorithm);
    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }


    public static void main(String[] args) throws Exception {
        String key = initBase64SecretKey();
        System.out.println("base64Key：" + key);
        System.out.println();

        String data = "AES数据";
        System.out.println("加密前数据: string : " + data);
        System.out.println();


        String encryptData = encrypt(data, key);
        System.out.println("加密后Base64数据 : " + encryptData);
        System.out.println("加密后数据 byte[] : " + Arrays.toString(Base64.decodeBase64(encryptData)));
        System.out.println("加密后数据16进制 :" + Hex.encodeHexString(Base64.decodeBase64(encryptData)));
        System.out.println();
        byte[] decryptData = decrypt(Base64.decodeBase64(encryptData), convertKey(key));
        System.out.println("解密后数据: string : " + new String(decryptData, "UTF-8"));
        String sourceData = decrypt(encryptData, key);
    }
}