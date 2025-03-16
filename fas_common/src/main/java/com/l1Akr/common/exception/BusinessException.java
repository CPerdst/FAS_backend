package com.l1Akr.common.exception;

import com.l1Akr.common.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    public final ResultEnum code;

    public BusinessException(ResultEnum code, Throwable cause) {
        super(code.message(), cause);
        this.code = code;
    }

    public BusinessException(ResultEnum code) {
        super(code.message());
        this.code = code;
    }

}
