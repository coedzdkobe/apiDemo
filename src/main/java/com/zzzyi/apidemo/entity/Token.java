package com.zzzyi.apidemo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Token {

    private String token;

    private Integer expireSecond;
}
