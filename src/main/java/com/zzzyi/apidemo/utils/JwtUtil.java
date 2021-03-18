package com.zzzyi.apidemo.utils;


import cn.hutool.core.util.RandomUtil;
import com.zzzyi.apidemo.entity.Token;
import com.zzzyi.apidemo.entity.TokenConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @Author: Brian
 * @Description JwtUtil
 * @DATE: 2021/3/4 16:15
 * @Version 1.0
 */
@Slf4j
public class JwtUtil {

    public static String SIGN_KEY = TokenConstant.SIGN_KEY;
    public static String BEARER = TokenConstant.BEARER;
    public static Integer AUTH_LENGTH = BEARER.length(); //BEARER 长度

    public static String BASE64_SECURITY = Base64.getEncoder().encodeToString(SIGN_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 获取去除头后真正的token信息
     * 认证头格式 bearer{auth}
     *
     * @param auth token
     * @return String
     * @see
     */
    public static String getToken(String auth) {
        if ((auth != null) && (auth.length() > AUTH_LENGTH)) {
            String headStr = auth.substring(0, 7).toLowerCase();
            if (headStr.compareTo(BEARER) == 0) {
                auth = auth.substring(7);
                return auth;
            }
        }
        return null;
    }

    /**
     * 解析token
     *
     * @param token token串
     * @return Claims
     */
    public static Claims parseJWT(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(JwtUtil.BASE64_SECURITY))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            log.info("JWT格式验证失败:{}", token);
            return null;
        }
    }


    /**
     * 创建令牌
     *
     * @param tokenType tokenType
     * @param params 需要添加的额外参数
     * @return jwt
     */
    public static Token createJWT(Map<String, Object> params, String tokenType) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签名密钥
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(BASE64_SECURITY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的类
        JwtBuilder builder = Jwts.builder().setHeaderParam("type", "JsonWebToken")
                .setIssuer("zzzzzYi_")
                .signWith(signatureAlgorithm, signingKey);


        //添加Token过期时间
        long expireMillis;
        if (tokenType.equals(TokenConstant.ACCESS_TOKEN)) {
            expireMillis = 2 * 3600 * 1000; // 2小时
        } else if (tokenType.equals(TokenConstant.REFRESH_TOKEN)) {
            expireMillis = 12 * 3600 * 1000; // 半天
        } else {
            expireMillis = 30 * 60 * 1000; // 30分钟
        }
        long expMillis = nowMillis + expireMillis;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp).setNotBefore(now);

        params.put(TokenConstant.TOKEN_TYPE,tokenType);
        params.put(TokenConstant.TOKEN_RANDOM, RandomUtil.randomNumbers(3));

        //设置JWT参数
        params.forEach(builder::claim);

        // 组装Token信息
        return new Token().setToken(builder.compact()).setExpireSecond((int) expireMillis / 1000);
    }

}
