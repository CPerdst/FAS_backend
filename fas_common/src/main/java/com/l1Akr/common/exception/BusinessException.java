package com.l1Akr.common.exception;

import com.l1Akr.common.result.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    public final ResultEnum code;

    public BusinessException(ResultEnum code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(ResultEnum code, String message) {
        super(message);
        this.code = code;
    }

}
