package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class DataPasswordErrorException extends BusinessException {
    public DataPasswordErrorException() {
        super(ResultEnum.DATA_PASSWORD_ERROR);
    }
}
