package com.nodecollege.cloud.common.model;

import com.nodecollege.cloud.common.exception.NCException;

/**
 * @author LC
 * @date 2019/12/13 18:21
 */
public interface CodeMessage {

    String getCode();

    String getMessage();

    /**
     * 返回错误
     *
     * @return
     */
    default NCResult error() {
        return NCResult.error(getCode(), getMessage());
    }

    /**
     * 构造异常错误
     *
     * @return
     */
    default NCException createError() {
        return new NCException(this.getCode(), this.getMessage());
    }

    /**
     * 抛出异常错误
     */
    default void throwMsg() {
        throw new NCException(this.getCode(), this.getMessage());
    }
}
