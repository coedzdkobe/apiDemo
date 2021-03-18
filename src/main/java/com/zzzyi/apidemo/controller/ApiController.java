package com.zzzyi.apidemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.zzzyi.apidemo.entity.Msg;
import com.zzzyi.apidemo.entity.Token;
import com.zzzyi.apidemo.entity.TokenUserInfo;
import com.zzzyi.apidemo.enums.CustomResponseStatus;
import com.zzzyi.apidemo.utils.AESCBCUtils;
import com.zzzyi.apidemo.utils.RSAUtils;
import com.zzzyi.apidemo.utils.SecurityUtil;
import com.zzzyi.apidemo.vo.TokenInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Brian
 * @Description 对外提供的Api
 * @DATE: 2021/3/18 19:39
 * @Version 1.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final static String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCZih7O/lGGa82DO13l3Pr33pYL1Nt4ExSjjFX8Y49jSj5AODEFDAvm3f8zGningWkdXF7rQPm8ojKmg68EHlZ9aUjgaFaupIrhZCG61X2bWmOb6UTo6jkr9AiXumuQ0ZKv8jUmf/2VqAtrGj+NbtHH0/9LxacFf3zn/RZ32ytRpLDANOFUcBGhYhjuqAeICW5JhBi+3Uw9HG9cEX/6hL4ZzxLr4QCYxQr/Yunssr4Xai7NV+ugAVj2GLhl1g5IWwyCNZS738fLdwu1hlGEUpbYE+0zmPpXCBdWAJFtuhGgMh+xbCBIvhSRiR1T3YvA3qT1JLoYObxuHAToVfoxYEjdAgMBAAECggEBAILCtLZItRmr2dy3xKIs7rgGovb0kuzSIzOeIuTNzaIb6qQ6ttS7i69UxTC6jKLrfZ/5uQTV01qpMEXLxFPXmoU4E066zYfyriyCYI+DnnOeKVLw+TT7tTEPpfnCVDWDpk+eEVH2kvrPoNnYAuPqy1JJ5BSeAj6WcsQIMQhmaaAfLauVwDYfV/l7kHPZtxHnUSqYAOmzm0Bgoas8GRZEyk+Ll0pRXxqhk8DVeXatV34x72rxKzZMpLf4GFgQ1eczoW02BAmjEUrUAYUX7gyHVuKc7bPJhgWGz4d/G2aRYO2PcQ6uStbm8Zodbjx/Jq/P9Azwu9TZFO8AP7JzoiRz9ikCgYEA+UMGOIDJGTleO7t/EaBNSVIVnmEM7czPt1z2cg/ZllcbzRxFgM61R5bhR7jNS0WFMrSghhLSpQPXrez6kDgb6CGmbLoROcSKi1eM05jH16cm45r2XlROCwDKosgmZLn+6kOLT17i3VbFMs8gCKAnHPK30LqaqdEqLcnpC1wf4xsCgYEAnbCqZj9SU96n5IEmnf1kMd+x3hT0/l3TPHW+0KopjUoUpyPvTNc0Lf3AMb8ImZROsNWlw8z5cnSC9YBKovezcqPgKQIv44EruO8XBlWiYUi0PBxGpc/nF/HjXkA+pm/YFlTBY/TY0bZxy3xJCHeDEdWn8DqxwCdWVLUnzyg5S2cCgYEAmR+3fpZ+82Puo7s3AFj9oYEvjrAIBT3AjOAq9T8PZ+/zQDmR6OMTMftaQXkhhJAcl12nDzYY+Q43PL1L37TYT+38EubrFhXLFnaeKI6+lZg1p7TWjHQ1zUSbNwzFqTMpijgcKJIdw+M+GUxsXWAlJv9kNHGt+Yxo4OAcrcHWROkCgYBdJVYCP7UpxYP2/jMJb/wXWcN/I3H0Lwfv1r14FIum4fOBJ0DbauNvp0w4FlfGIEy7N/hKyHEwopIY4kc0LIzbUuG+V4RiJpLlEpXPMBZS0NdmCm3Q1BX1lvRvYBOhzL8fscDqgl7wwYxGXovlbHMHveUNih4lGxdrwfZZ77CoUQKBgHjSlBSqqongM/ZJz05AXR1gIQdfiKQ++xfbFfZhshMsOooNAaWe04RYd4h6obBHYANS+B0kqYutN4EUTxZULBSf7FWdYFwSq9lMJycQuczIrmizaue7H5AJk3doF324yQTRC1X4DV4im/vYKw/pDpKtBj/hedkMrFyxEK3utP9l";

    /**
     * 生成token
     * @param channelId 豆沙包分发给渠道的id
     * @param channelSecret 豆沙包分发给渠道的secret
     * @return
     */
    @RequestMapping("/token")
    public Msg getToken(String channelId, String channelSecret) {
        try {
            log.info("获取token的请求参数channelId-----{},channelSecret-----{}", channelId, channelSecret);
            //根据业务校验channelId和channelSecret是否合法

            TokenUserInfo tokenInfo = new TokenUserInfo();
            tokenInfo.setChannelId(channelId);
            tokenInfo.setChannelSecret(channelSecret);
            //生成accessToken
            Token accessToken = SecurityUtil.createAccessTokenByUserInfo(tokenInfo);
            TokenInfoVo tokenInfoVo = new TokenInfoVo();
            tokenInfoVo.setAccessToken(accessToken.getToken());
            tokenInfoVo.setExpiresTime(accessToken.getExpireSecond());
            return new Msg(CustomResponseStatus.OK.getCode(), CustomResponseStatus.OK.getDescription(), tokenInfoVo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取token异常{}", e.getMessage());
            return new Msg(CustomResponseStatus.SERVER_ERROR.getCode(),CustomResponseStatus.SERVER_ERROR.getMessage());
        }
    }

    /**
     * 测试签名
     * @param jsonParam
     * @param request
     * @return
     */
    @PostMapping("/testSign")
    private Msg checkMWSToken(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
        try {
            String encodeKey = request.getHeader("encodeKey");
            String encryptRequestBody = jsonParam.getString("encryptRequestBody");

            //获取渠道设置的aesKey
            String aesKey = RSAUtils.decrypt(encodeKey,PRIVATE_KEY);
            log.info("渠道的aesKey:{}", aesKey);

            //解密后的json报文
            String contentJson = AESCBCUtils.decrypt(encryptRequestBody,aesKey);
            log.info("解密后的json报文：{}", contentJson);

            //业务逻辑处理

            //处理结果信息加密返回给接口提供方
            Object decryptData = AESCBCUtils.encrypt("it is ok!", aesKey);
            return new Msg(Msg.SUCCESS_CODE, "处理成功", decryptData);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("接口异常-->{}", e.getMessage());
            return new Msg(CustomResponseStatus.SERVER_ERROR.getCode(), CustomResponseStatus.SERVER_ERROR.getMessage());
        }
    }
}
