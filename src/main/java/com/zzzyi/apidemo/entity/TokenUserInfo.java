package com.zzzyi.apidemo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * auth返回的数据
 * 其中的重要数据会加密到access token中
 * @see
 */
@Data
@Accessors(chain = true)
public class TokenUserInfo {

    private String tokenId;

    //token生效时间
    private String expiresTime;

    //token 类型
    private String tokenType;

    private String channelId;

    private String channelSecret;
}
