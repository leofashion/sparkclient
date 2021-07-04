package com.sweet.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DESedeCoder {

    private static String secretKey = "ugfpV1dMC5jyJtqwVAfTpHkxqJ0+E0ae";
    /**
     * 秘钥算法 java6只支持56位秘钥
     */
    public static final String KEY_ALGORITHM = "DESede";

    /**
     * 加密/加密算法
     */
    public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";


    public static void main(String[] args) throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException {

        String estr = "liruiqing:admin";
        System.out.println(DESedeCoder.encrypt(estr));
        String jdon = "FkKvQ0xVuYpG278w3t%5D%5DSQ==";
        System.out.println(jdon);
        System.out.println(DESedeCoder.decrypt(jdon));
    }
    /**
     * 转换秘钥
     *
     * @param key
     *            二进制秘钥（key就是秘密秘钥二进制字节数组的形式，但我们要使用它需要将它转换成秘钥对象，
     *            首先需要将二进制秘钥转换成秘钥材料对象ps:这里是DESKeySpec的dks，再使用秘钥工厂生产秘钥SecretKeyFactory）
     * @return Key 秘钥
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        // 实例化DES秘钥材料
        DESedeKeySpec dks = new DESedeKeySpec(key);
        // 实例化秘密秘钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        // 生产密码秘钥
        SecretKey secretKey = keyFactory.generateSecret(dks);
        return secretKey;
    }

    /**
     * 解密
     *
     * @param data
     *            待解密数据
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data) throws Exception {
        // 还原秘钥
        Key k = toKey(secretKey.getBytes());
        // 实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 加密
     *
     * @param data
     *            待加密数据
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data) throws Exception {
        Key k = toKey(secretKey.getBytes());
        // 实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化 设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 生产秘钥 java 6只支持56位秘钥
     *
     * @return byte[] 二进制秘钥
     * @throws Exception
     */
    public static byte[] initKey() throws Exception {
        /**
         * 实例化秘钥生产器 目前java 6 支持的是56位
         */
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        /**
         * 初始化秘钥生产 若要使用64位秘钥生产器直接把56换成64即可。 秘钥长度和安全性成正比
         */
        kg.init(168);
        // 生产秘密秘钥
        SecretKey sky = kg.generateKey();
        // 获得秘钥的二进制编码格式
        return sky.getEncoded();
    }



    public static class EncPairs {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey;

        SecretKeyFactory keyFactory;
        SecretKey secretKey;
        // 加密
        Cipher cipher;

        public EncPairs(String key) {
            try {
                desKey = new DESKeySpec(key.getBytes());
                keyFactory = SecretKeyFactory.getInstance("DES");
                secretKey = keyFactory.generateSecret(desKey);
                // 加密
                cipher = Cipher.getInstance("DES");
            } catch (Exception e) {

            } finally {

            }
        }
    }
    public static String encryptBase64(String jsonStr) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(jsonStr.getBytes("UTF-8")));
    }
    public static String decryptBase64(String jsonStr) throws UnsupportedEncodingException {
        return new String(Base64.decodeBase64(jsonStr.getBytes("UTF-8")), "UTF-8");
    }

    static ThreadLocal<HashMap<String, EncPairs>> encContext = new ThreadLocal<>();

    public static String encrypt(String jsonStr) {
        try {
            HashMap<String, EncPairs> epMap = encContext.get();
            if (epMap == null) {
                epMap = new HashMap<String, EncPairs>();
                encContext.set(epMap);
            }
            EncPairs ep = epMap.get(secretKey);
            if (ep == null) {
                ep = new EncPairs(secretKey);
                epMap.put(secretKey, ep);
            }
            ep.cipher.init(Cipher.ENCRYPT_MODE, ep.secretKey, ep.random);
            return encryptFormat(new String(Base64.encodeBase64(ep.cipher.doFinal(jsonStr.getBytes("UTF-8")))));
        } catch (Exception e) {
            return null;
        }
    }

    public static String encryptFormat(String str){

        return str = str.replaceAll("[+]", "[").replaceAll("[/]", "]");
    }


    public static String decryptFormat(String str){
        return str = str.replaceAll("[\\[]", "+").replaceAll("[\\]]", "/");
    }

    public static String decrypt(String jsonStr) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
        UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

        jsonStr = decryptFormat(jsonStr);
        HashMap<String, EncPairs> epMap = encContext.get();
        if (epMap == null) {
            epMap = new HashMap<String, EncPairs>();
            encContext.set(epMap);
        }
        EncPairs ep = epMap.get(secretKey);
        if (ep == null) {
            ep = new EncPairs(secretKey);
            epMap.put(secretKey, ep);
        }
        ep.cipher.init(Cipher.DECRYPT_MODE, ep.secretKey, ep.random);
        return new String(ep.cipher.doFinal(Base64.decodeBase64(jsonStr.getBytes("UTF-8"))), "UTF-8");
    }

}
