package com.nodecollege.cloud.common.exception;

import com.nodecollege.cloud.common.enums.ErrorEnum;

/**
 * 自定义异常
 *
 * @author LC
 * @date 20:04 2019/11/27
 **/
public class NCException extends BaseException {
    private static final long serialVersionUID = 8912473097017237022L;

    public NCException() {
        super();
    }

    public NCException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public NCException(ErrorEnum error) {
        super(error);
    }

    public NCException(ErrorEnum error, String description) {
        super(error, description);
    }
}