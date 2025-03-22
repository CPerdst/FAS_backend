package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class UserNotExistException extends BusinessException {
    public UserNotExistException() {
        super(ResultEnum.USER_NOT_EXIST);
    }
}
