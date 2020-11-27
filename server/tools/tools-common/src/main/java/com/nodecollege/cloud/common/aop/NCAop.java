package com.nodecollege.cloud.common.aop;

import ch.qos.logback.classic.util.LoggerNameUtil;
import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.MDCUtils;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.ThreadPoolFactory;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * ClassName: ServiceDigestAop <br/>
 * 检测方法执行耗时的spring切面类 <br/>
 * Function: 服务摘要拦截器，输出service层服务性能数据<br/>
 * date: 2017年10月24日 下午2:28:32 <br/>
 *
 * @author wuyuegang
 * @since JDK 1.8
 */
//@Aspect
//@Configuration
public class NCAop {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ExecutorService NC_AOP_EXECUTOR = ThreadPoolFactory.getInstance();
    /**
     * service层的统计耗时切面，类型必须为final String类型的,注解里要使用的变量只能是静态常量类型的
     * service
     * service.impl
     * service.impl.*.
     */
    private static final String SERVICE_POINT = ""
            + "execution (* com.nodecollege..*.service.*(..))"
            + "|| execution (* com.nodecollege..*.service.*.*(..))"
            + "|| execution (* com.nodecollege..*.service.*.*.*(..))"
            + "|| execution (* com.nodecollege..*.service.*.*.*.*(..))";

    /**
     * controller-web包切面，类型必须为final String类型的,注解里要使用的变量只能是静态常量类型的
     */
    private static final String CONTROLLER_POINT = ""
            + "execution (* com.nodecollege..*.controller.*(..))"
            + "|| execution (* com.nodecollege..*.controller.*.*(..))"
            + "|| execution (* com.nodecollege..*.controller.*.*.*(..))";

    /**
     * 统计方法执行耗时Around环绕通知
     *
     * @param joinPoint joinPoint
     * @author LC
     * @date 20:16 2019/11/10
     **/
    @Around(CONTROLLER_POINT)
    public Object controllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始时间
        long startTime = System.currentTimeMillis();
        try {
            handle();
        } catch (Exception e){
            // 不影响正常业务
            logger.error("requestId解析错误", e);
        }

        // 请求参数
        Object[] args = joinPoint.getArgs();
        // 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 返回参数
        Object retObject = joinPoint.proceed();

        Map mdcAdapter = MDC.getCopyOfContextMap();
        try {
            NC_AOP_EXECUTOR.submit(() -> cA(args, signature, retObject, startTime, mdcAdapter));
        } catch (Exception e) {
            // 不影响正常业务
            logger.error("日志解析出错", e);
        }
        return retObject;
    }

    /**
     * 统计方法执行耗时Around环绕通知
     *
     * @param joinPoint joinPoint
     * @author LC
     * @date 20:16 2019/11/10
     **/
    @Around(SERVICE_POINT)
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始时间
        long startTime = System.currentTimeMillis();
        // 请求参数
        Object[] args = joinPoint.getArgs();
        // 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 返回参数
        Object retObject = joinPoint.proceed();
        Map mdcAdapter = MDC.getCopyOfContextMap();
        try {
            NC_AOP_EXECUTOR.submit(() -> sA(args, signature, retObject, startTime, mdcAdapter));
        } catch (Exception e) {
            // 不影响正常业务
            logger.error("日志解析出错", e);
        }
        return retObject;
    }

    /**
     * 处理requestId
     */
    private void handle(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 获取请求头里面的 nc_request_id
        String requestId = request.getHeader(NCConstants.NC_REQUEST_ID);
        String threadId = request.getHeader(NCConstants.NC_THREAD_ID);
        String threadId2 = request.getHeader(NCConstants.NC_THREAD_ID2);
        if (StringUtils.isBlank(requestId)) {
            // 为空自动生成一个
            requestId = NCUtils.getRequestId();
        }
        // 设置到MDC中，在构造NcResult时自动插入到requestId字段中
        MDC.put(NCConstants.REQUEST_ID, requestId);
        MDC.put(NCConstants.THREAD_ID, threadId);
        MDC.put(NCConstants.THREAD_ID2, threadId2);
        logger.debug("请求url:[{}],requestId:[{}]", request.getRequestURI(), requestId);
    }

    private void cA(Object[] args, MethodSignature signature, Object retObject, Long startTime, Map mdcAdapter) {
        MDCUtils.handle(mdcAdapter);
        // 获取执行的方法名
        String methodName = getConciseMethodName(signature.getDeclaringTypeName()) + "_" + signature.getName();
        logger.debug("[{}] 开始 入参 [{}]", methodName, generateParamDigest(args));

        // 打印耗时的信息
        logger.debug("[{}] 结束 耗时[{}ms] 出参 [{}] ", methodName, (System.currentTimeMillis() - startTime), generateParamDigest(retObject));
    }

    private void sA(Object[] args, MethodSignature signature, Object retObject, Long startTime, Map mdcAdapter) {
        MDCUtils.handle(mdcAdapter);
        String methodName = getConciseMethodName(signature.getDeclaringTypeName()) + "_" + signature.getName();
        logger.debug("[{}] 开始 入参 [{}]", methodName, generateParamDigest(args));

        // 打印耗时的信息
        logger.debug("[{}] 结束 耗时[{}ms] 出参 [{}] ", methodName, (System.currentTimeMillis() - startTime), generateParamDigest(retObject));
    }

    private static String generateParamDigest(Object[] args) {
        if (args == null) {
            return "args is null !";
        }
        StringBuffer argString = new StringBuffer();
        for (Object arg : args) {
            argString.append(generateParamDigest(arg));
        }
        return argString.toString();
    }

    private static String generateParamDigest(Object arg) {
        if (arg == null) {
            return "arg is null !";
        }
        StringBuffer argString = new StringBuffer();
        if (isPrimite(arg.getClass())) {
            argString.append(arg.toString()).append(",");
        } else if (arg instanceof ServletResponse) {
            // 不是基本类型,输出classname
            argString.append(arg.getClass().getName()).append(",");
        } else {
            argString.append(JSONObject.toJSONString(arg)).append(",");
        }
        return argString.toString();
    }

    /**
     * 获取的全包名的简写
     *
     * @param methodName
     * @return
     */
    public static String getConciseMethodName(String methodName) {
        List<String> list = LoggerNameUtil.computeNameParts(methodName);
        StringBuilder ss = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            ss.append(list.get(i).substring(0, 1)).append(".");
        }
        if (list.size() > 0) {
            ss.append(list.get(list.size() - 1));
        }
        return ss.toString();
    }

    /**
     * 判断是否为基本类型：包括String
     *
     * @param clazz clazz
     * @return true：是; false：不是
     */
    private static boolean isPrimite(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz == String.class) {
            return true;
        } else {
            return false;
        }
    }

}
