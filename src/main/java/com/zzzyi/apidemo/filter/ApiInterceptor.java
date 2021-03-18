package com.zzzyi.apidemo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzzyi.apidemo.entity.Msg;
import com.zzzyi.apidemo.enums.CustomResponseStatus;
import com.zzzyi.apidemo.utils.JsonUtil;
import com.zzzyi.apidemo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeMap;

/**
 * @Author: Brian
 * @Description
 * @DATE: 2021/3/17 15:42
 * @Version 1.0
 */
@Slf4j
@Component
public class ApiInterceptor extends GenericFilterBean {

    private static final String TEST_SIGN = "/api/testSign";

    @Override
    public void doFilter(ServletRequest res, ServletResponse req, FilterChain chain) throws IOException, ServletException {
        CustomHttpServletRequestWrapper request = null;
        if (res instanceof HttpServletRequest) {
            request = new CustomHttpServletRequestWrapper((HttpServletRequest) res);
        }

        HttpServletResponse response = (HttpServletResponse) req;
        response.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        log.info("请求的uri为：" + uri);

        if (uri.startsWith(TEST_SIGN)) {
            Msg msg;
            try {
                //开始校验
                String timestamp = request.getHeader("timestamp");
                String stringBuffer = new String(request.getBody());
                JSONObject jsonObject = JSON.parseObject(stringBuffer);
                if (null == jsonObject) {
                    msg = new Msg(CustomResponseStatus.BAD_REQUEST_PARAM.getCode(), CustomResponseStatus.BAD_REQUEST_PARAM.getMessage());
                    response.getWriter().write(JsonUtil.objectTojson(msg));
                    return;
                }
                String encryptRequestBody = String.valueOf(jsonObject.get("encryptRequestBody"));
                log.info("encryptRequestBody:{}", encryptRequestBody);

                //校验报文是否过期（3分钟以内的有效）
                if (!checkTimestamp(timestamp)) {
                    log.info("报文已过期---");
                    msg = new Msg(CustomResponseStatus.FORBIDDEN.getCode(), CustomResponseStatus.FORBIDDEN.getDescription());
                    response.getWriter().write(JsonUtil.objectTojson(msg));
                    return;
                }

                //校验access_token是否有效和签名是否正确
                msg = checkSign(encryptRequestBody, request);
                if (!msg.success()) {
                    response.getWriter().write(JsonUtil.objectTojson(msg));
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                msg = new Msg(CustomResponseStatus.BAD_REQUEST_PARAM.getCode(), CustomResponseStatus.BAD_REQUEST_PARAM.getMessage());
                response.getWriter().write(JsonUtil.objectTojson(msg));
                return;
            }
        }
        //校验通过
        chain.doFilter(request, req);
    }

    private Msg checkSign(String encryptRequestBody, HttpServletRequest request) {
        TreeMap<String, String> deMap = new TreeMap<String, String>();
        //获取请求头信息
        String authorization = request.getHeader("Authorization");
        String timestamp = request.getHeader("timestamp");
        String encodeKey = request.getHeader("encodeKey");
        String version = request.getHeader("version");
        String sign = request.getHeader("sign");
        String format = request.getHeader("format");
        String token = JwtUtil.getToken(authorization);
        log.info("请求头的authorization：{}--------access_token:{}", authorization, token);
        if (null == token) {
            return new Msg(CustomResponseStatus.ILLEGAL_TOKEN.getCode(),CustomResponseStatus.ILLEGAL_TOKEN.getDescription());
        }
        Claims claims = JwtUtil.parseJWT(token);
        if (null == claims) {
            return new Msg(CustomResponseStatus.ILLEGAL_TOKEN.getCode(),CustomResponseStatus.ILLEGAL_TOKEN.getDescription());
        }
        String channelId = (String)claims.get("channelId");
        String channelSecret = (String)claims.get("channelSecret");
        log.info("client_id----->{}", channelId);
        log.info("channelSecret----->{}", channelSecret);

        //签名
        deMap.put("signMethod", "sha256");
        deMap.put("client_id", channelId);
        deMap.put("secret", channelSecret);
        deMap.put("format", format);
        deMap.put("version", version);
        deMap.put("encodeKey", encodeKey);
        deMap.put("timestamp", timestamp);
        deMap.put("requestData", encryptRequestBody);
        log.info("参与签名的参数：{}", deMap.toString());
        String sha256Sign = DigestUtils.sha256Hex(deMap.toString());
        log.info("参数中获取的sign-------{}", sign);
        log.info("验证签名产生的sign------{}", sha256Sign);
        if (!sign.equals(sha256Sign)) {
            return new Msg(CustomResponseStatus.BAD_SIGN.getCode(),CustomResponseStatus.BAD_SIGN.getMessage());
        }
        return new Msg(Msg.SUCCESS_CODE, channelId);
    }

    public boolean checkTimestamp(String timestamp) {
        long currentTime = System.currentTimeMillis();
        long sendTime = Long.valueOf(timestamp);
        if (currentTime - sendTime > 3*60*1000) {
            return false;
        }
        return true;
    }
}
