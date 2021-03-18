package com.zzzyi.apidemo.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzzyi.apidemo.advice.CommonException;
import com.zzzyi.apidemo.entity.Token;
import com.zzzyi.apidemo.entity.TokenConstant;
import com.zzzyi.apidemo.entity.TokenUserInfo;
import com.zzzyi.apidemo.enums.CustomResponseStatus;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关util
 */
@Slf4j
public class SecurityUtil {

    public static final String USER_INFO_ATTR = "USER_INFO_ATTR";

    public static Token createAccessTokenByUserInfo(TokenUserInfo tokenUserInfo) {
        Map<String, Object> params = buildUserParam(tokenUserInfo);
        return JwtUtil.createJWT(params, TokenConstant.ACCESS_TOKEN);
    }

    private static Map<String, Object> buildUserParam(TokenUserInfo tokenUserInfo) {
        Map<String, Object> params = new HashMap<>();

        String tokenId = tokenUserInfo.getTokenId();
        String expiresTime = tokenUserInfo.getExpiresTime();
        String tokenType = tokenUserInfo.getTokenType();
        String channelId = tokenUserInfo.getChannelId();
        String channelSecret = tokenUserInfo.getChannelSecret();

        params.put(TokenConstant.TOKEN_ID, tokenId);
        params.put(TokenConstant.EXPIRES_TIME, expiresTime);
        params.put(TokenConstant.TOKEN_TYPE, tokenType);
        params.put(TokenConstant.CHANNEL_ID, channelId);
        params.put(TokenConstant.CHANNEL_SECRET, channelSecret);

        return params;
    }


    /**
     * 获取Claims
     * <p>
     * 前端是带有bearer的
     *
     * @param request request
     * @return Claims
     */
    public static Claims getClaims(HttpServletRequest request) {
        String auth = request.getHeader(TokenConstant.BASIC_HEADER_KEY);
        String token = JwtUtil.getToken(auth);
        return JwtUtil.parseJWT(token);
    }

    /**
     * 获取用户信息
     *
     * @return TokenUserInfo
     */
    public static TokenUserInfo getUser() {
        String userInfoJson = RequestUtil.getHeader(TokenConstant.USER_INFO_HEADER);

        ObjectMapper bean = SpringContextHolder.getBean(ObjectMapper.class);
        TokenUserInfo tokenUserInfo;
        try {
            tokenUserInfo = bean.readValue(userInfoJson, TokenUserInfo.class);
        } catch (IOException e) {
            log.error("getUser error,", e);
            throw new CommonException(CustomResponseStatus.BAD_REQUEST);
        }
        return tokenUserInfo;
    }

}
