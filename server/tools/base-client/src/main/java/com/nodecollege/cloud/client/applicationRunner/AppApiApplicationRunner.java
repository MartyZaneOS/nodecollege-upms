package com.nodecollege.cloud.client.applicationRunner;

import com.nodecollege.cloud.client.feign.SyncApiClient;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.aop.NCAop;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/10/23 20:19
 */
@Slf4j
@Order(-98)
@Component
public class AppApiApplicationRunner implements ApplicationRunner {

    @Value("${spring.application.name:default}")
    private String applicationName;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private SyncApiClient syncApiClient;

    @Override
    public void run(ApplicationArguments args) {
        log.info("初始化查询api信息！");
        if ("operate".equals(applicationName)) {
            return;
        }
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        List<OperateAppApi> apis = new ArrayList<>((int) Math.ceil(handlerMethods.size() / 0.75) + 1);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : handlerMethods.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            ApiAnnotation apiAnnotation = method.getMethodAnnotation(ApiAnnotation.class);
            OperateAppApi api = new OperateAppApi();
            api.setApplicationName(applicationName);
            api.setControllerName(NCAop.getConciseMethodName(method.getMethod().getDeclaringClass().getName()));
            api.setApiUrl(info.getPatternsCondition().getPatterns().iterator().next());
            if (apiAnnotation != null) {
                api.setModularName(apiAnnotation.modularName());
                api.setDescription(apiAnnotation.description());
                api.setState(apiAnnotation.state());
            } else {
                api.setModularName(api.getControllerName());
                api.setDescription(api.getApiUrl());
                api.setState(1);
            }
            log.info("api信息 {}", api.toString());
            api.setAccessAuth("none");
            apis.add(api);
        }
        syncApiClient.initApiList(apis);
        log.info("同步接口完成");
    }
}
