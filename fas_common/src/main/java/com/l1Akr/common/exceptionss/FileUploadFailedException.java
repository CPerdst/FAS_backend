package com.l1Akr.common.exceptionss;

import com.l1Akr.common.enums.ResultEnum;

public class FileUploadFailedException extends BusinessException {

    public FileUploadFailedException() {
        super(ResultEnum.FILE_UPLOAD_FAILED);
    }

}
