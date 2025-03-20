package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ResultEnum.USER_NOT_EXIST);
    }
}
