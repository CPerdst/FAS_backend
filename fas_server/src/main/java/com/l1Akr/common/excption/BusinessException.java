package com.l1Akr.common.excption;

import com.l1Akr.common.result.Result.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    public final ResultEnum code;

    public BusinessException(ResultEnum code) {
        super(code.getMessage());
        this.code = code;
    }

}
