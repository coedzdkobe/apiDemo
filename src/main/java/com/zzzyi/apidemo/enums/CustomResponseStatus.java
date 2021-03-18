package com.zzzyi.apidemo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回前端状态码
 */
@AllArgsConstructor
@Getter
public enum CustomResponseStatus {

    OK(200, "ok","成功"),
    SERVER_ERROR(500, "internal server error","服务器异常"),
    BAD_REQUEST(400, "bad request",""),

    NOT_FOUND(404, "not found",""),
    CONFLICT(409, "conflict",""),
    DATA_INTEGRITY_VIOLATION_EXCEPTION(424, "data integrity violation exception",""),


    /* ============================     custom status code    =================================================  */

    UNAUTHORIZED(401, "unauthorized","没有登录/或者登录已过期，security模块使用 "),
    ILLEGAL_TOKEN(4011, "Illegal access_token","无效的access_token"),
    TOKEN_EXPIRED(4012, "access Token expired","token过期"),
    REFRESH_TOKEN_EXPIRE(4013, "refresh token expire","refresh token过期"),

    GET_TOKEN_FAILED(4001,"获取Token失败", "获取Token失败"),

    FORBIDDEN(403, "Forbidden","请求不被允许"),

    USER_NOT_FOUND(4041, "user not found","用户未找到"),

    UPLOAD_ERROR(500, "upload error","文件上传失败"),

    DSB_SAAS_SERVER_ERROR(100500, "网络异常！", "网络异常！"),
    DSB_SAAS_EXCEPTION(100400, "SAAS中心返回异常！", "SAAS中心返回异常！"),
    DSB_ACOUNT_SERVER_ERROR(200500, "网络异常！", "网络异常！"),
    DSB_ACOUNT_RESULT_EXCEPTION(200400, "账户中心返回异常！", "账户中心返回异常！"),

    SESSION_USER_NOT_FOUNT(4011, "网络异常！", "网络异常！"),

    BAD_REQUEST_PARAM(4002, "非法的请求参数", "非法的请求参数"),

    BAD_SIGN(4003, "签名不合法", "签名不合法")


    ;

    /**
     * 状态码
     */
    private int code;
    /**
     * 信息
     */
    private String message;

    /**
     * 描述
     */
    private String description;


}
