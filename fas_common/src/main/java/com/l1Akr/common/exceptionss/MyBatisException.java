package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class MyBatisException extends SystemException{

    public MyBatisException() {
        super(ResultEnum.ERROR);
    }

}
