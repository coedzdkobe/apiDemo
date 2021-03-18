package com.zzzyi.apidemo.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * @Author: Brian
 * @Description RSA 加解密签名工具类
 * @DATE: 2021/3/4 16:15
 * @Version 1.0
 */
@Slf4j
public class RSAUtils {

    public static final String KEY_ALGORITHM = "RSA";

    public static final String ENCODE_CHARSET = "UTF-8";

    public static final String RSA_PADDING_KEY = "RSA/ECB/PKCS1Padding";

    /**
     * RSA 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

    /**
     * 生成公私钥长度
     */
    private static final int KEY_SIZE = 2048;
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = KEY_SIZE / 8 - 11;
    
    public static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 获取RSA公钥
     *
     * @param key 公钥字符串
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * RSA公钥加密
     *
     * @param plainText   待加密数据
     * @param s_publicKey 公钥字符串
     * @return
     */
    public static String encrypt(String plainText, String s_publicKey) {
        if (plainText == null || s_publicKey == null) {
            return null;
        }
        try {
            PublicKey publicKey = getPublicKey(s_publicKey);
            Cipher cipher = Cipher.getInstance(RSA_PADDING_KEY);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(plainText.getBytes(ENCODE_CHARSET));
            return formatString(Base64.encodeBase64String(enBytes));
        } catch (Exception e) {
            log.error("RSA encrypt Exception:" + e);
        }
        return null;
    }

    /**
     * 格式化RSA加密字符串,去掉换行和渐近符号
     *
     * @param sourceStr
     * @return
     */
    private static String formatString(String sourceStr) {
        if (sourceStr == null) {
            return null;
        }
        return sourceStr.replaceAll("\\r", "").replaceAll("\\n", "");
    }

    /**
     * 获取RSA私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * RSA私钥解密
     *
     * @param enStr        待解密数据
     * @param s_privateKey 私钥字符串
     * @return
     */
    public static String decrypt(String enStr, String s_privateKey) {
        if (enStr == null || s_privateKey == null) {
            return null;
        }
        try {
            PrivateKey privateKey = getPrivateKey(s_privateKey);
            Cipher cipher = Cipher.getInstance(RSA_PADDING_KEY);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(Base64.decodeBase64(enStr));
            return new String(deBytes, ENCODE_CHARSET);
        } catch (Exception e) {
            log.error("RSA decrypt Exception:" + e);
        }
        return null;
    }

    /**
     * RSA签名
     * <p>
     * MD5摘要RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 关联方私钥
     * @return
     */
    public static String sign(String content, String privateKey) {
        if (content == null || privateKey == null) {
            return null;
        }
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(content.getBytes(ENCODE_CHARSET));
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception e) {
            log.error("RSA sign Exception:" + e);
        }
        return null;
    }

    /**
     * RSA签名验证
     * <p>
     * MD5摘要RSA签名验证
     *
     * @param content   待签名数据
     * @param sign      签名值
     * @param publicKey 分配给关联方公钥
     * @return 布尔值
     */
    public static boolean verifySign(String content, String sign, String publicKey) {
        if (content == null || sign == null || publicKey == null) {
            return false;
        }
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(ENCODE_CHARSET));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            log.error("RSA verifySign Exception:" + e);
        }
        return false;
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    public static String toHexString(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append(toHexString(data[i]));
        }
        return sb.toString();
    }

    public static String toHexString(byte b) {
        int tmp = b & 0xFF;
        int high = (tmp & 0xf0) / 16;
        int low = (tmp & 0x0f) % 16;
        return new String(new char[]{HEX_CHAR[high], HEX_CHAR[low]});
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 用来生成 RSA 密钥对
     * @throws Exception
     */
    public static void createKeyPairs() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        // create the keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(KEY_SIZE, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PrivateKey privKey = pair.getPrivate();
        byte[] pk = publicKey.getEncoded();
        byte[] privk = privKey.getEncoded();
        String strpk = new String(Base64.encodeBase64(pk));
        String strprivk = new String(Base64.encodeBase64(privk));

        System.out.println("公钥:" + Arrays.toString(pk));
        System.out.println("私钥:" + Arrays.toString(privk));
        System.out.println("公钥Base64编码:" + strpk);
        System.out.println("私钥Base64编码:" + strprivk);

        X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(strpk.getBytes()));
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(strprivk
                .getBytes()));

        KeyFactory keyf = KeyFactory.getInstance("RSA", "BC");
        PublicKey pubkey2 = keyf.generatePublic(pubX509);
        PrivateKey privkey2 = keyf.generatePrivate(priPKCS8);

        System.out.println(publicKey.equals(pubkey2));
        System.out.println(privKey.equals(privkey2));
    }

    //main
    public static void main(String[] args) throws Exception {
        createKeyPairs();
    //    String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsDLug47p3j/vPK1cwYMQIf3Lr7Rpx+QhMmD95isiXRo96dYVk2cObqEUeCLiJzQA7Vfatfeu2gnzFWd1ySa+8P3l3etrnCmnhO7hQpEeLopiFTOPPRMIieYsM76/ToWvhbwoKgTcstF9fOzp6xKaVrLAKST9kNYSgg3GDq+ZdkLoCoz2dBsNKMtQZcxxzg9V3EC8j3YvMOTcIYaI0fWKRBrN5PH7XxN2vX72j1SfFg0DD2sdQ0K06YxQpWl8GBWjjnpT0/q5F4TGKJHdrlgUbkgUiYzNMkxP+pMtmeCwVVaYXSwEThqNKAYgbKq0QWM/mHzQFnYJ8MV6YrpEHFAwMwIDAQAB";
     //   String priKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCwMu6DjuneP+88rVzBgxAh/cuvtGnH5CEyYP3mKyJdGj3p1hWTZw5uoRR4IuInNADtV9q1967aCfMVZ3XJJr7w/eXd62ucKaeE7uFCkR4uimIVM489EwiJ5iwzvr9Oha+FvCgqBNyy0X187OnrEppWssApJP2Q1hKCDcYOr5l2QugKjPZ0Gw0oy1BlzHHOD1XcQLyPdi8w5NwhhojR9YpEGs3k8ftfE3a9fvaPVJ8WDQMPax1DQrTpjFClaXwYFaOOelPT+rkXhMYokd2uWBRuSBSJjM0yTE/6ky2Z4LBVVphdLAROGo0oBiBsqrRBYz+YfNAWdgnwxXpiukQcUDAzAgMBAAECggEAHAP3y+/fnENFfRFXi+aUnG2n2EfIVfceQXi4hhivqGaxbMppuHviCJG7bnVQHh70/XKYR5Hv5E1BWv2N9djKSAkmu8gLM9OlBiGcAXiPeE5FKs0oO57DdHb7lOgBr7DZPVtdy33lwCzyVfhZd9NdD5HxCdm5fjD7f7WZmzYtIWddRi/iXY2Nr5O3bOgEKAnJl7xNvRAweO13j4IPO5utQ3r+XZ0dvBeGuU3LLAGtm/lr6bOyGjcgRJDK9vXcat/T2BCwr9E7Fi6akBx+weLrZSiaTvRSNTPYgqCUAj3AnC/Di9rvPY/LTE1hL6JCmlE7HVj+A4kZgz3OhJQN6sYHQQKBgQDv9KGnHyYKQj25sLRQWxD8Tg/OsLminvkTenjxVEfYP/Gd+dg9ijNz24RYLUbfMOVYR0fJgXUznYYORKTkfXPH6yNHpeSqZfFT5GtvrT83n0TSDQakHDUS1U2iSdN9T4VxXroaTIv0geecr0irvNCYeD0hDkZCtoKyqHVECt56EwKBgQC7+vcD013E0un0JXRYxgY+uSVFyaBygEyui9eANZE9yMRf7r/O6shfsbUyL2QtxNTtUtJ25hMuverN9Znq6QVJ+QBfMm/Fg4UQeHgNygZp6dUHY2R/zes0b+e0sl4nAsLvM+AWjx8WqSHs+DgO6FY2+FDlhIC5+jbQSLJtbgo1YQKBgBoEimqWhAdku1ChCkwUM7lHtYsRum8ZkaQ9CVQRcWDPPiGE759slrufN7Bf6GlW4ec6g1wj/1NDZz+wzbhAqgcpRcAFCtA3EX2pLMUFIe+kA1BLPUcaD98k5bANFTNnJe7u+HrVhe9TDov8rYKBjJSbpPUqmQ+uvmkkvuJL02elAoGAJN0OW92zYcnKv2oo6s/KobpFKO5lXI70erZJyIa4uNkKSZXUX4uDR+Ddm73Xc3JKH1BCnbZ4xI3R4hnr2TlhJCSEgiat7JniGlzFjk6z+0kz0YMYTkfffCUF11WzSQstlyMhKsteWV9Jm+MBe0rsvVLs6lWVuOLspOPcucD+l4ECgYAH76iEpg46OXDC9In5Y+qZ0LIsH1zp5VY3iOfU8sH/ST2f0xznzEt98hhd2qb5oOSV750Wh5V6I8ayCgQS53BKrMxdrsqIWFIIaVije2WgNfP7Icp3dXAq7O/eEtNjPYYhNNNILCe0+BTeoznr5MtcoZUwv81h4sWzqAieB0UYdA==";
    //    String data = encrypt("test1111", pubKey);
      //  System.out.println("密文:" + data);
//        System.out.println("密文:" + data);
//        data = "Ye+NjtJzEQ9i6S+pgizK61JK3ViJ2I301UNO8x8V+Lz8SlOMqo2su4E4hGN8ASL4GQNyQhsiDZkjfSEjQqNAm4niDA2eNXaL4sErrJtqvNxXZedEb73NH78RuwjJzVEc/Ql2pLKMsQ55NyimnnA8ef95FK0WnvsIqYbJcieS0pbBK/0mn2I9ue/Dji2cpZQhIKQRt19HIY/ssihnvNDrxRNTUJDlNbijIdq7iMmBjrk2TaE2XFGXgIBmv31yLwiGVl0t5Wo+lCU0h/jTBmx2puPE+ODXgxNQSF8ZgSy4pbf8EooTzBacPX2AqQhsQNoTDgjRgbDx7UVDuOeW2mkehg==";
    //    String mingwen = decrypt(data, priKey);
      //  System.out.println("明文:" + mingwen);
//
//        String qianming = sign("123456", priKey);
//        System.out.println("签名:" + qianming);
//        System.out.println(verifySign("123456", qianming, pubKey));
//
//        String sha256Sign = DigestUtils.sha256Hex("123456");
//        System.out.println("sha256签名:" + sha256Sign);
//        System.out.println(sha256Sign.equals(DigestUtils.sha256Hex("123456")));
    }
}
