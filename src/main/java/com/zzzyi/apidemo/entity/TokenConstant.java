package com.zzzyi.apidemo.entity;

/**
 * Token配置常量.
 */
public interface TokenConstant {

    /**
     * Jwt 加密key
     */
    String SIGN_KEY = "aUth_sIgn";

    /**
     * Jwt 头钱
     */
    String BEARER = "zzzzzyi";

    /**
     * 前端认证请求头
     */
    String BASIC_HEADER_KEY = "Authorization";


    /**
     * access_token
     */
    String ACCESS_TOKEN = "access_token";

    /**
     * refresh_token 时效比access_token长 默认 1天 24*60*60s
     * access_token 失效时，使用 refresh_token刷新token
     */
    String REFRESH_TOKEN = "refresh_token";

    String TOKEN_TYPE = "token_type";

    String USER_INFO_HEADER = "zzzzzyi_User_Info";

    /**
     * 生成token时，添加随机数
     */
    String TOKEN_RANDOM = "random";

	/**
	 * jwt中claims加入的参数
	 */
	String TOKEN_ID = "tokenId";
    String CHANNEL_ID = "channelId";
    String CHANNEL_SECRET = "channelSecret";
    //token生效时间
    String EXPIRES_TIME = "expiresTime";


    /**
     * 过期时间 过期时间秒数
     * 用于前端实现定时器，在access_token结束的时候 使用refresh_token刷新access_token
     * access_token 2小时 2 * 3600 * 1000
     */
    int ACCESS_TOKEN_EXPIRES_SECOND = 2 * 3600 * 1000;

    /**
     * refresh_token 12小时
     */
    int REFRESH_TOKEN_EXPIRES_SECOND = 12 * 60 * 60;

}
