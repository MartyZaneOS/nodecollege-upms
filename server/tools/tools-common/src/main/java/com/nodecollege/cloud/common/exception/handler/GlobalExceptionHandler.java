package com.nodecollege.cloud.common.exception.handler;

import com.nodecollege.cloud.common.exception.BaseException;
import com.nodecollege.cloud.common.model.NCResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常拦截器处理类,采用 <b>@ExceptionHandler</b>注解拦截异常
 *
 * @author LC
 * @date 20:22 2019/11/27
 **/
@ControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {
    /**
     * 404的异常就会被这个方法捕获
     *
     * @param req
     * @param rsp
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public NCResult handle404Error(HttpServletRequest req, HttpServletResponse rsp, Exception e)
            throws Exception {
        return handleError(req, e);
    }

    /**
     * 500的异常会被这个方法捕获
     *
     * @param req
     * @param rsp
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public NCResult handle500Error(HttpServletRequest req, HttpServletResponse rsp, Exception e)
            throws Exception {
        return handleError(req, e);
    }

    /**
     * 自定义异常会被这个方法捕获
     *
     * @param req
     * @param rsp
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public NCResult handleMyException(HttpServletRequest req, HttpServletResponse rsp,
                                      BaseException e)
            throws Exception {
        return handleMyError(req, e);
    }
}