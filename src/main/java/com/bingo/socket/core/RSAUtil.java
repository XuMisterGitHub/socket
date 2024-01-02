package com.bingo.socket.core;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class RSAUtil {

    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static final String ECB_PADDING = "RSA/ECB/PKCS1Padding";// 加密填充方式
    // public static final String ECB_PADDING = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";//加密填充方式
    /**
     * RSA算法规定：待加密的字节数不能超过密钥的长度值除以8再减去11。 而加密后得到密文的字节数，正好是密钥的长度值除以 8。
     */
    private static final int KEY_SIZE = 2048;// 密钥位数
    private static final int RESERVE_BYTES = 11;
    private static final int DECRYPT_BLOCK = KEY_SIZE / 8;
    private static final int ENCRYPT_BLOCK = DECRYPT_BLOCK - RESERVE_BYTES;

    private static final String PUBLIC_KEY_STR = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfZ/tUHIxPS7ZGxsvIy2Ok/y0nzD6Aw3nW5b46YI/JU6d/m0AI+KX5Tf70PeojdJqvUXgWWNPZo2Rq6RZX3GXPqwKhoIus+j/ix2maCzMkdvaK/vRnS5QYpZZNne12wJjCErVbRw4HCNC+hLN3lLqhusf35ks9T0biOg6U7IwT5QIDAQAB";
    private static final String PRIVATE_KEY_STR = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAN9n+1QcjE9LtkbGy8jLY6T/LSfMPoDDedblvjpgj8lTp3+bQAj4pflN/vQ96iN0mq9ReBZY09mjZGrpFlfcZc+rAqGgi6z6P+LHaZoLMyR29or+9GdLlBillk2d7XbAmMIStVtHDgcI0L6Es3eUuqG6x/fmSz1PRuI6DpTsjBPlAgMBAAECgYAxjRapqUtVqy1atW0ttC79wbt6QEr8tF6p03tF53T86pkULJMfakcl7K7H7ZOpGYvVpvRDwJ/8fckgDWLvSesqoi1Mue3HMfWxY7kvnXV2kvDs07r8b3EjU0NRLePF7JZQHXgnpIdFMFIaHfVElsE7X1bS6AWs7oMtYNb863DEYQJBAP7/5In7xWIX6E0b9jmYxUpQ+Ns2W9Hxhv4tmlSjuOxFDX3kDec/3htGzGL1cF20WW+CBynE15twU3ZG2I6fJRcCQQDgSFu+4iphr5kYws2L6cN7afpRQO4gfk+joPqOYOvlBLkqBFsZYPBL8t37tmAL8zl9iGbS6p/1NbtbTXs7cqRjAkBei6xFyNbPXwPfiy2I/26u9kS+qnHy7nVZyri9BxMqbxPoiGTrR8/nFyHy0wO9in/ai0ByqSwz0rrvUKc8gh8zAkB1umW2R/+ZMVR6o97DP5ymDpYfyqZ0lfj12k1LKWMJ3zXW8VxkcPcR1tVhCHVYMH8P/uaCdjgqvd6v6XbzWJUjAkAUbKoX6AhO+5aipsItE3YF/tTEmMhI/IjY0TCmmgclGDWVA9FKqbVL68E1p8dOUUgQtWOkbNeLUk57Y1sZ2rWq";

    /**
     * 随机生成RSA密钥对
     *
     * @param keySize 密钥长度，范围：512-2048,一般2048
     */
    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keySize);
            return kpg.genKeyPair();
        } catch (Exception e) {
            log.error("generateKeyPair exception.", e);
            return null;
        }
    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param data 原文
     */
    public static byte[] encryptWithPublicKey(byte[] data, byte[] key) throws Exception {
        Cipher cp = Cipher.getInstance(ECB_PADDING);
        cp.init(Cipher.ENCRYPT_MODE, getPublicKey(key));
        return cp.doFinal(data);
    }

    /**
     * 用公钥对字符串进行加密
     */
    public static String encryptWithPublicKey(String plain, String publicKey) throws Exception {
        byte[] key = Base64.decodeBase64(publicKey);
        byte[] data = plain.getBytes("UTF-8");
        return Base64.encodeBase64String(encryptWithPublicKey(data, key));
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     */
    public static byte[] decryptWithPublicKey(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     */
    public static byte[] encryptWithPrivateKey(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     */
    public static byte[] decryptWithPrivateKey(byte[] data, byte[] key) throws Exception {
        Cipher cp = Cipher.getInstance(ECB_PADDING);
        cp.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        byte[] arr = cp.doFinal(data);
        return arr;
    }

    public static PublicKey getPublicKey(byte[] key) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKey(byte[] key) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 分块加密
     *
     * @param plain
     * @param publicKey
     */
    public static String encryptWithPublicKeyBlock(String plain, String publicKey) throws Exception {
        byte[] data = plain.getBytes("UTF-8");
        byte[] key = Base64.decodeBase64(publicKey);

        byte[] result = encryptWithPublicKeyBlock(data, key);
        return Base64.encodeBase64String(result);
    }

    /**
     * 分块解密
     *
     * @param data
     * @param key
     */
    public static String decryptWithPrivateKeyBlock(String plain, String privateKey) {
        try {
            byte[] data = Base64.decodeBase64(plain.getBytes("UTF-8"));
            byte[] key = Base64.decodeBase64(privateKey);

            byte[] bytes = decryptWithPrivateKeyBlock(data, key);
            return new String(bytes);
        } catch (Exception e) {
            log.error("decryptWithPrivateKeyBlock exception.", e);
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        Map<Integer, String> map = genKeyPair();


        //String publicKey = map.get(0);
        //String privateKey = map.get(1);
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0wMppbvJO9rfvoxdqrjfOfo0qDIA7ifa3tORaGIA0Y+3QxBZMCbn19ruUlvqvkV4o8RZFtrQV3iMQV/pXJA40EmmeNSSV+wyvFV96XhClelqa8gJnyw9SRahpl4GVeMvo/4Ph0VsQpygGGvjNdn0sgbzf1BNfOesNjk5IqTyUXNCo0Kc4vMscJ9kt+NxoTHNRyaeHXSxn/Est3q4Uf8Eecv6y/Hh9OcnnvQyhxG9s5b6SfRFcGfYXZxUeffb49FxTC3vaLF5Gegai0LA9/jK9XBJ0nunA2KN3V5BiFz2VmFD9+yrrCvB7NtSgrwlL5fnB8ImB/cOoycNtoJG38seuwIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDTAymlu8k72t++jF2quN85+jSoMgDuJ9re05FoYgDRj7dDEFkwJufX2u5SW+q+RXijxFkW2tBXeIxBX+lckDjQSaZ41JJX7DK8VX3peEKV6WpryAmfLD1JFqGmXgZV4y+j/g+HRWxCnKAYa+M12fSyBvN/UE1856w2OTkipPJRc0KjQpzi8yxwn2S343GhMc1HJp4ddLGf8Sy3erhR/wR5y/rL8eH05yee9DKHEb2zlvpJ9EVwZ9hdnFR599vj0XFMLe9osXkZ6BqLQsD3+Mr1cEnSe6cDYo3dXkGIXPZWYUP37KusK8Hs21KCvCUvl+cHwiYH9w6jJw22gkbfyx67AgMBAAECggEAe9GHRrECTBUK5Y0ICX2hvyuKaoes9BJ+4/HRiSa4xD9IkhmtvAuYT5kv7zPwcuXg/Hfirv6G8kzSQCo/jQkzzUTqYkyZ8u2ud8TStYTTzZPB1qzyuGwFKjYVdMIHha5h7iDOi9m4xP2TE8F6+caDR1O83iAI2Bo5ezfoxuNzLUcbyHtgVf8O10mTZFKcp2kJU/T6SrFwSP5wx31qSiHcLdAKz9iiPmHIot+SV8URA6/o/6KnQARMfVVohkUr0nRdUa6LlemFAOx4NO7qWWu2WLVI0EjP/RSlILsX3ajjK2LLiGm14BlYwS0J3tONC/b/RxBFGGUI4CAHHLdPc25lAQKBgQD15K4WkZUgf5d4W7h7/nVdJUCHchINQL7S7rRhC/bRW13iwH16lvc73MwKvsM4h0dASAHDEz5vEbvGsx/0+rDFcDdw8ngx5qlJ8+qML5RdMQm5RimpOwfkqTO2frc1Ehf8kRiAs2ciLaMYYlS7pHgxkrX5c7shTnG+CB4v4CmT6wKBgQDbr3YOlweiPXM3Mr+JTNmeAWySHklFxgq3eYrXrQ3bJIa/RK595pB+SXpmk0ScA8cYOEwKGEdPncWRRGHfnfuNusJEs/dhpugpCh3KHARAJqIYJzruCHFwjqVmZIzFw9jdB1wWdM5B8mm8C3A6GItDgMRN90aEOR2k6XZGiaF8cQKBgQDiSv8vw/EjBGlImdiRPB+Uhkj5nbAhRDmZc5bLTTBrgZNIr++Kl06WZ9/b761H469d6Ca++YHWw9I0zfqWAPQX7+sF5g9ZlTas2W48bzkx/oQ9XuXOWYYtGM5/n+RJUgqDJKERKIm8eZWERlJGkdkPfj48gRZxU3j38w6c7sY02wKBgFbSgJkBjY3MNXXSQ6qSycrNi0gwWgsOWvvqODpG9ZvvUqXTEhdGAUM37P0PJOV4BL6IuVyf/cb0drmTyg6jlGCON+sJKTLZ0RUvH1jaO7/SxOtAzeR3R8YRSkMuaAhc/59TIkzpuv6LPMiJyL/dJWT3Rh3+JUtqe1HKxujXyg+xAoGBAOoFgjHPV9SdlAJ7X2otwwY+x2uSJWyLDU/vprTM2Lrkq9ybhw+OgGFud/yIcYoU62tvZZoOFg+sWvDY1EA0AsBJShvQiarKItr2fIugSfw5UFU75fJT49DLEId2mohJ56iR+NnMZRYQM9sMTqH+H3otKHKi7oz1n9La0doEpq/t";


        System.out.println("公钥：" + publicKey);
        System.out.println("私钥：" + privateKey);

        //String s = "测试rsa明文数据";
        //String s1 = RSAUtil.encryptWithPublicKeyBlock("", publicKey);

        //System.out.println("明文：" + s);
        //System.out.println("加密后：" + s1);


        //私钥加密
        String s1 = "服务端的私钥加密数据";
        byte[] data = s1.getBytes("UTF-8");
        byte[] key = Base64.decodeBase64(privateKey);
        byte[] result = encryptWithPrivateKeyBlock(data, key);
        s1 = Base64.encodeBase64String(result);
        System.out.println("私钥加密后：" + s1);


        String s2 = "GMoIvt+3rcc7VzF1gCmad/5ICJ6RAQ8wheph8rLOgEUpSdtx0ni8mv/PR4GDkAjcTs6L45G89dMniuQqn129a82nWir4zWuwSvDym+nKvwV0Ga5cCc6Uoyp0qe2dmMy23+Ar1aBRKqr/r5MVwW/ANE2IwXbaG4kVevXj507ZkePAq/iiYDi08Gi5h+V4dbGEZVKSv6UoaKipiF9OOv1h7sa5jXwBwLOLGliphRgPOaH2xOKI4+SbjZc+GUS1xozrcnDVk7I/9smNJ1Ij6IsxdOTdL50TFy8xm6N4Ya9IPlxGGEwORhzrRpOQI0qeaxS0uc2CkRu1I3VNLLBX41SKtA==";
        System.out.println("私钥解密后：" + decryptWithPrivateKeyBlock(s2, privateKey));


    }

    public static Map<Integer, String> genKeyPair() {

        Map<Integer, String> keyMap = new HashMap<Integer, String>(); // 用于封装随机产生的公钥与私钥

        try {
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = generateKeyPair(KEY_SIZE);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 得到公钥

            // 得到公钥字符串
            String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
            // 得到私钥字符串
            String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));

            // 将公钥和私钥保存到Map
            keyMap.put(0, publicKeyString); // 0表示公钥
            keyMap.put(1, privateKeyString); // 1表示私钥
        } catch (Exception e) {
            log.info("生成公钥私钥异常：" + e.getMessage());
            return null;
        }

        return keyMap;
    }

    /**
     * 分块加密
     *
     * @param data
     * @param key
     */
    public static byte[] encryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块加密
     *
     * @param data
     * @param key
     */
    public static byte[] encryptWithPrivateKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块解密
     *
     * @param data
     * @param key
     */
    public static byte[] decryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / DECRYPT_BLOCK);
        if ((data.length % DECRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * DECRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
        for (int offset = 0; offset < data.length; offset += DECRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > DECRYPT_BLOCK) {
                inputLen = DECRYPT_BLOCK;
            }
            byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(decryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块解密
     *
     * @param data
     * @param key
     */
    public static byte[] decryptWithPrivateKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / DECRYPT_BLOCK);
        if ((data.length % DECRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * DECRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        for (int offset = 0; offset < data.length; offset += DECRYPT_BLOCK) {
            int inputLen = (data.length - offset);

            if (inputLen > DECRYPT_BLOCK) {
                inputLen = DECRYPT_BLOCK;
            }

            byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(decryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }
}
