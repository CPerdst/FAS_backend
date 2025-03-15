package com.l1Akr.config;

import com.l1Akr.common.exception.BusinessException;
import com.l1Akr.common.exception.SystemException;
import com.l1Akr.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public Result<String> doBusinessException(BusinessException e) {
        log.warn("doBusinessException has occurred: {}", e.getMessage());
        return new Result<>(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    public Result<String> doSystemException(SystemException e) {
        log.warn("doSystemException has occurred: {}", e.getMessage());
        return new Result<>(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> doException(Exception e) {
        log.warn("doException has occurred: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
