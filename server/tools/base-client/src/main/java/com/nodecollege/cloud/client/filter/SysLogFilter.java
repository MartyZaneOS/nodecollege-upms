package com.nodecollege.cloud.client.filter;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.constants.QueueConstants;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.SysLog;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.QueueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class SysLogFilter {

    @Value("${spring.application.name:default}")
    private String applicationName;

    @Value("${log.push:false}")
    private Boolean logPush;

    @Value("${log.exclude.urls:[/article-static]}")
    private List<String> logExcludeUrls;

    @Autowired
    private QueueUtils queueUtils;

    @Bean
    public FilterRegistrationBean reqResFilter() {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(((request, response, chain) -> {
            long startTime = System.currentTimeMillis();
            SysLog sysLog = new SysLog();
            sysLog.setAppName(applicationName);
            sysLog.setCreateTime(new Date());

            HttpServletRequest req = (HttpServletRequest) request;

            sysLog.setRequestIp(NCUtils.getRequestIp(req));
            sysLog.setAccessSource(req.getHeader(HeaderConstants.ACCESS_SOURCE));
            sysLog.setRequestUri(req.getRequestURI());
            if (sysLog.getRequestUri().lastIndexOf("/") == sysLog.getRequestUri().length() - 1){
                sysLog.setRequestUri(sysLog.getRequestUri().substring(0, sysLog.getRequestUri().length() - 1));
            }
            sysLog.setReferer(req.getHeader("Referer"));

            // 获取访问请求id 用于链路追踪
            String requestId = req.getHeader(HeaderConstants.REQUEST_ID);
            // 获取服务调用随机码
            String callIds = req.getHeader(HeaderConstants.CALL_IDS);
            if (StringUtils.isBlank(requestId)) {
                requestId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
            }
            // 每个服务在后面追加字符串
            callIds = (StringUtils.isBlank(callIds) ? "" : callIds + "-") + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 3);
            sysLog.setRequestId(requestId);
            sysLog.setCallIds(callIds);
            MDC.put(HeaderConstants.REQUEST_ID, requestId);
            MDC.put(HeaderConstants.CALL_IDS, callIds);

            log.info("requestIp:{}, url:{}",sysLog.getRequestIp(), sysLog.getRequestUri());
            // todo
            // 开启日志上传功能
            if (logPush && !checkLogExcludeUrls(sysLog.getRequestUri())) {
                ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response);//转换成代理类

                if ("POST".equals(req.getMethod())) {
                    RequestWrapper requestWrapper = new RequestWrapper(req);
                    chain.doFilter(requestWrapper, wrapperResponse);
                    sysLog.setInParam(requestWrapper.getBody());
                } else {
                    sysLog.setInParam(req.getQueryString());
                    chain.doFilter(request, wrapperResponse);
                }
                log.debug("请求参数：{}", sysLog.getInParam());

                byte[] content = wrapperResponse.getContent();//获取返回值

                //判断是否有值
                if (content.length > 0) {
                    String responseBody = new String(content, "UTF-8");
                    log.debug("返回值:" + responseBody);
                    if (responseBody.length() > 1024 * 8) {
                        // 返回值太大 截取部分
                        sysLog.setOutParam(responseBody.substring(0, 1024 * 8));
                    } else {
                        sysLog.setOutParam(responseBody);
                    }
                    try {
                        NCResult result = JSONObject.parseObject(responseBody, NCResult.class);
                        sysLog.setSuccess(result.getSuccess());
                    } catch (Exception e) {
                        log.error("非标准返回值，转换异常！返回值：{}", responseBody, e);
                    }
                    //把返回值输出到客户端
                    ServletOutputStream out = response.getOutputStream();
                    out.write(content);
                    out.flush();
                    out.close();
                }

                sysLog.setLostTime(System.currentTimeMillis() - startTime);
                log.info("花费:{}ms", sysLog.getLostTime());
                try {
                    queueUtils.pushTask(QueueConstants.SYS_LOG, sysLog);
                } catch (Exception e) {
                    log.error("保存日志到队列中失败！ {}", JSONObject.toJSONString(sysLog), e);
                }
            } else {
                chain.doFilter(request, response);
                sysLog.setLostTime(System.currentTimeMillis() - startTime);
                log.info("花费:{}ms", sysLog.getLostTime());
            }
        }));
        filterBean.addUrlPatterns("/*");//配置过滤规则
        filterBean.setOrder(-999);
        return filterBean;
    }

    // 校验是否是日志不记录地址
    private boolean checkLogExcludeUrls(String url) {
        for (String item : logExcludeUrls) {
            if (url.indexOf(item) == 0) {
                return true;
            }
        }
        return false;
    }
}