package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class UserHasExistedException extends BusinessException {
    public UserHasExistedException() {
        super(ResultEnum.USER_HAS_EXISTED);
    }
}
