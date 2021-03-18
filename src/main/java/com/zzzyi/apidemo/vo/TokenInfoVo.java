package com.zzzyi.apidemo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Brian
 * @Description
 * @DATE: 2021/3/4 14:48
 * @Version 1.0
 */
@Data
public class TokenInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;

    private Integer expiresTime;

   // private String tokenType;

}
