package com.nodecollege.cloud.common.exception.handler;

import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.BaseException;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 全局异常拦截器处理基类，对自定义异常和未知异常进行统一封装，返回标准错误信息给用户</br>
 * <b>返回结果示例：</b>{"success":false,"errorCode":"-1","errorMsg":"系统异常，请稍后再试","data":[]}</br>
 *
 * @author LC
 * @date 20:17 2019/11/27
 **/
public class BaseGlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String DEFAULT_ERROR_MESSAGE = "系统忙，请稍后再试";

    /**
     * 异常处理方法
     */
    protected NCResult handleError(HttpServletRequest req, Exception e)
            throws Exception {
        NCResult result = null;
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        if (e instanceof UndeclaredThrowableException) {
            e = (Exception) ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        if (e instanceof BaseException) {
            result = new NCResult((BaseException) e);
            logger.error("Request: {} raised exception,error:", req.getRequestURI(), e);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            // 请求方法不正确
            logger.error("Request: {} raised exception,error:", req.getRequestURI(), e);
            NCException sysException = new NCException(ErrorEnum.REQUEST_METHOD_ERROR);
            result = new NCResult<>(sysException);
        } else {
            // 拦截未知异常
            logger.error("Request: {} raised exception,error:", req.getRequestURI(), e);
            NCException sysException = new NCException(ErrorEnum.UNKNOWN_ERROR);
            result = new NCResult<>(sysException);
        }
        return result;
    }

    /**
     * 异常处理方法
     */
    protected NCResult handleMyError(HttpServletRequest req, BaseException e) throws Exception {
        String errorMsg = e.getMessage();
        String errorCode = e.getCode();
        NCResult result = new NCResult(e);
        logger.error("Request: {} raised exception errcode [{}] errmsg [{}]", req.getRequestURI(), errorCode, errorMsg);
        return result;
    }
}
