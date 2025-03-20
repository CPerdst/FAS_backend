package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemException extends RuntimeException {

    private final ResultEnum code;

    public SystemException(ResultEnum code) {
        super(code.message());
        this.code = code;
    }
}
