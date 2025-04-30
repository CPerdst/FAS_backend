package com.l1Akr.config;

import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.excption.SystemException;
import com.l1Akr.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        log.warn("doException has occurred: {} {}", e.getClass().getSimpleName(), e.getMessage());
        return Result.error("internal error occurred");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleSizeExceeded(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "文件大小超过限制");
        return "redirect:/upload";
    }
}
