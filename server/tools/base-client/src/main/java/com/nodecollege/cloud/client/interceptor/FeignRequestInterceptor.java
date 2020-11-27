package com.nodecollege.cloud.client.interceptor;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.constants.HeaderConstants;
import com.nodecollege.cloud.common.model.MemberLoginInfo;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.UserLoginInfo;
import com.nodecollege.cloud.common.utils.MemberLoginThreadUtils;
import com.nodecollege.cloud.common.utils.UserLoginThreadUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author LC
 * @date 2020/1/10 9:55
 */
@Slf4j
@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Value("${spring.application.name:default}")
    private String applicationName;

    @Autowired
    private NCLoginUtils loginUtils;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(HeaderConstants.ACCESS_SOURCE, applicationName);
        String requestId =  MDC.get(HeaderConstants.REQUEST_ID);
        if (requestId == null) {
            // 服务自启动，或者定时任务运行调用其他服务接口
            return;
        }

        requestTemplate.header(HeaderConstants.REQUEST_ID, requestId);
        requestTemplate.header(HeaderConstants.CALL_IDS, MDC.get(HeaderConstants.CALL_IDS));

        NCLoginUserVO loginAdmin = loginUtils.getAdminLoginInfo();
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        if (loginAdmin != null) {
            requestTemplate.header(HeaderConstants.ADMIN_ACCESS_TOKEN, loginAdmin.getAccessToken());
            requestTemplate.header(HeaderConstants.ADMIN_UUID, loginAdmin.getUuid());
        }
        if (loginUser != null) {
            requestTemplate.header(HeaderConstants.USER_ACCESS_TOKEN, loginUser.getAccessToken());
            requestTemplate.header(HeaderConstants.USER_UUID, loginUser.getUuid());
        }
        if (loginMember != null) {
            requestTemplate.header(HeaderConstants.MEMBER_ACCESS_TOKEN, loginMember.getAccessToken());
            requestTemplate.header(HeaderConstants.MEMBER_UUID, loginMember.getUuid());
        }
    }
}
