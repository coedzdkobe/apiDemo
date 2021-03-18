package com.zzzyi.apidemo.advice;


import com.zzzyi.apidemo.enums.CustomResponseStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = 6801519635531297845L;

    /**
     * @see CustomResponseStatus
     */
    private Integer code;

    private String message;

    private Integer httpStatus;

    public CommonException(CustomResponseStatus responseStatus){
        this.message = responseStatus.getMessage();
        this.code = responseStatus.getCode();
        this.httpStatus = CustomResponseStatus.SERVER_ERROR.getCode();
    }

    public CommonException(CustomResponseStatus responseStatus, HttpStatus httpStatus){
        this.message = responseStatus.getMessage();
        this.code = responseStatus.getCode();
        this.httpStatus = httpStatus.value();
    }

    public CommonException(CustomResponseStatus responseStatus, String message){
        this.message = message;
        this.code = responseStatus.getCode();
        this.httpStatus = CustomResponseStatus.SERVER_ERROR.getCode();
    }

}
