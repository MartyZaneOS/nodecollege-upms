package com.nodecollege.cloud.common.exception;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import lombok.Data;

/**
 * @author LC
 * @date 19:53 2019/11/27
 **/
@Data
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 8177301895983119239L;
    /**
     * 错误代码
     */
    String code;
    /**
     * 错误消息
     */
    String message;
    /**
     * 错误描述
     */
    String description;

    public BaseException() {
        super(ErrorEnum.UNKNOWN_ERROR.getMessage());
        this.code = ErrorEnum.UNKNOWN_ERROR.getCode();
        this.message = ErrorEnum.UNKNOWN_ERROR.getMessage();
    }

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(String code, String message, String description) {
        super(message + description);
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public BaseException(ErrorEnum error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public BaseException(ErrorEnum error, String description) {
        super(error.getMessage() + description);
        this.code = error.getCode();
        this.message = error.getMessage();
        this.description = description;
    }
}