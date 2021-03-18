package com.zzzyi.apidemo.test;

import com.alibaba.fastjson.JSONObject;
import com.zzzyi.apidemo.utils.AESCBCUtils;
import com.zzzyi.apidemo.utils.RSAUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.TreeMap;

/**
 * @Author: Brian
 * @Description
 * @DATE: 2021/3/4 16:15
 * @Version 1.0
 */
public class TestReq {

    //可自行设置长度为16位的AES_KEY，每个请求可以设置不同的AES_KEY
    private static final String AES_KEY = "woijsqersgnskewq";

    //公钥
    private static final String PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmYoezv5RhmvNgztd5dz6996WC9TbeBMUo4xV/GOPY0o+QDgxBQwL5t3/Mxp4p4FpHVxe60D5vKIypoOvBB5WfWlI4GhWrqSK4WQhutV9m1pjm+lE6Oo5K/QIl7prkNGSr/I1Jn/9lagLaxo/jW7Rx9P/S8WnBX985/0Wd9srUaSwwDThVHARoWIY7qgHiAluSYQYvt1MPRxvXBF/+oS+Gc8S6+EAmMUK/2Lp7LK+F2ouzVfroAFY9hi4ZdYOSFsMgjWUu9/Hy3cLtYZRhFKW2BPtM5j6VwgXVgCRbboRoDIfsWwgSL4UkYkdU92LwN6k9SS6GDm8bhwE6FX6MWBI3QIDAQAB";

    //服务端提供
    private static final String CHANNEL_ID = "test";

    //服务端提供
    private static final String CHANNEL_SECRET = "ddddddddddddddddddddd";

    //获取token
    private static final String TOKEN_URL = "http://127.0.0.1:8090/api/token";

    //请求测试接口
    private static final String TEST_SIGN = "http://127.0.0.1:8090/api/testSign";

    public static void main(String[] args) throws IOException{
        //拼接json
        String json = "{\"test\":\"eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee898d4f6sa4f65\"}";
        System.out.println(json);
        sendHttpRequest(json);
    }

    private static void sendHttpRequest(String requestBody) throws IOException {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("signMethod", "sha256");
        treeMap.put("secret", CHANNEL_SECRET);
        treeMap.put("client_id", CHANNEL_ID);
        treeMap.put("version", "1.0");
        treeMap.put("format", "json");

        String encodeKey = RSAUtils.encrypt(AES_KEY, PUB_KEY);
        treeMap.put("encodeKey", encodeKey);
        System.out.println("encodeKey------>"+encodeKey);
        //当前时间戳
        String timestamp = String.valueOf(System.currentTimeMillis());
        treeMap.put("timestamp", timestamp);
        System.out.println("timestamp----->"+timestamp);
        //使用 AES 算法加密报文体，使用报文体密文进行签名
        String encryptRequestBody = AESCBCUtils.encrypt(requestBody, AES_KEY);
        System.out.println("encryptRequestBody------>"+encryptRequestBody);
        if (encryptRequestBody != null) {
            treeMap.put("requestData", encryptRequestBody);
            System.out.println("requestData----->"+encryptRequestBody);
        }
        String sha256Sign = DigestUtils.sha256Hex(treeMap.toString());
        System.out.println("sign----------->"+sha256Sign);
        System.out.println("treeMap----------->"+treeMap.toString());

        System.out.println("requestBody---->明文 : " + requestBody);
        System.out.println("requestBody---->密文 : " + encryptRequestBody);

        HttpPost httpPost = new HttpPost(TEST_SIGN);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Accept", "application/json;charset=UTF-8");

        httpPost.addHeader("timestamp", timestamp);
        httpPost.addHeader("format", "json");
        httpPost.addHeader("version", "1.0");
        httpPost.addHeader("encodeKey", encodeKey);
        httpPost.addHeader("sign", sha256Sign);

        //通过TOKEN_URL接口调用
        String accessToken = "eyJ0eXBlIjoiSnNvbldlYlRva2VuIiwiYWxnIjoiSFMyNTYifQ.eyJpc3MiOiJ6enp6ellpXyIsImV4cCI6MTYxNjA3NzMxOCwibmJmIjoxNjE2MDcwMTE4LCJyYW5kb20iOiI5MjQiLCJ0b2tlbl90eXBlIjoiYWNjZXNzX3Rva2VuIiwiY2hhbm5lbElkIjoidGVzdCIsImNoYW5uZWxTZWNyZXQiOiJkZGRkZGRkZGRkZGRkZGRkZGRkZGQifQ.F2ni9ReqgQszGbr_RlgO9ornvnsSi6YX1wMzB2Kx7d4";
        httpPost.addHeader("Authorization", "zzzzzYi " + accessToken);
        String json = "{\"encryptRequestBody\":\""+encryptRequestBody+"\"}";
        System.out.println(json);
        HttpEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        CloseableHttpClient httpCLient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpCLient.execute(httpPost);
        entity = httpResponse.getEntity();
        String result = IOUtils.toString(entity.getContent());
        System.out.println(result);

        JSONObject response = JSONObject.parseObject(result);
        String data = response.getString("data");
        if (data != null && data.length() > 0) {
            //使用AES 算法解密响应报文
            String decryptData = AESCBCUtils.decrypt(data, AES_KEY);
            response.put("data", decryptData);
            System.out.println("解密后的报文："+decryptData);
        }
        System.out.println(response.toString());
    }

}
