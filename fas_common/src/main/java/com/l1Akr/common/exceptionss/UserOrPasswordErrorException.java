package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class UserOrPasswordErrorException extends BusinessException {
    public UserOrPasswordErrorException() {
        super(ResultEnum.USER_LOGIN_ERROR);
    }
}
