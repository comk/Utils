package com.mayhub.utils.encrypt;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



/**
 * Created by comkdai on 2018/1/27.
 */
public class AESEncrypt {

    private static volatile AESEncrypt instance;

    public static AESEncrypt getInstance() {
        if(instance == null){
            synchronized(AESEncrypt.class){
                if(instance == null){
                    instance = new AESEncrypt();
                }
            }
        }
        return instance;
    }

    /**
     * 加密
     * @param content
     * @param strKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String strKey, String ivStr) throws Exception {
        SecretKeySpec skeySpec = getKey(strKey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        return base64Encode(cipher.doFinal(content.getBytes()));
    }

    /**
     * 解密
     * @param strKey
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(String content, String strKey, String ivStr) throws Exception {
        SecretKeySpec skeySpec = getKey(strKey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(base64Decode(content));
        return new String(original);
    }

    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");

        return skeySpec;
    }


    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return Base64.decode(base64Code, Base64.DEFAULT);
    }

}
