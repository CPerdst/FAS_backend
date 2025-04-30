package com.l1Akr.common.excption;

import com.l1Akr.common.result.Result.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemException extends RuntimeException {

    private final ResultEnum code;

    public SystemException(ResultEnum code) {
        super(code.getMessage());
        this.code = code;
    }
}
