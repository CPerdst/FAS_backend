package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    public final ResultEnum code;

    public BusinessException(ResultEnum code) {
        super(code.message());
        this.code = code;
    }

}
