package com.nodecollege.cloud.core.netty;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.annotation.NCController;
import com.nodecollege.cloud.common.annotation.NCRequestMapping;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.exception.BaseException;
import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.core.SpringContextHolder;
import com.nodecollege.cloud.feign.UserClient;
import com.nodecollege.cloud.utils.ChatLoginUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LC
 * @date 2020/2/26 23:07
 */
@Slf4j
@Component
public class ChatDispatchServlet extends ChatAbstrat {

    private static final Map<String, NCRequestHandler> NC_BEANS = new ConcurrentHashMap<>();

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ChatLoginUtils chatLoginUtils;

    @Value("${spring.application.name:default}")
    private String applicationName;

    // 白名单 不校验是否登陆
    private static final List<String> whiteUrl = Arrays.asList("/debate/debateConnect",
            "/debate/getRecordList",
            "/debate/getUserInfo",
            "/debate/getUpUserList");

    /**
     * 初始化ncBean
     */
    private void initNCBeans() {
        final Map<String, Object> ncMap = SpringContextHolder.getApplicationContext().getBeansWithAnnotation(NCController.class);
        for (String key : ncMap.keySet()) {
            final NCRequestMapping ncMapping = AnnotationUtils.findAnnotation(ncMap.get(key).getClass(), NCRequestMapping.class);
            String requestUrl = "";
            if (null != ncMapping && StringUtils.isNotBlank(ncMapping.value())) {
                requestUrl = ncMapping.value();
            }
            Method[] methods = ncMap.get(key).getClass().getDeclaredMethods();
            for (Method method : methods) {
                //获取指定方法上的注解的属性
                final NCRequestMapping methodMapping = AnnotationUtils.findAnnotation(method, NCRequestMapping.class);
                if (null != methodMapping) {
                    NCRequestHandler handler = new NCRequestHandler();
                    handler.setClazz(ncMap.get(key).getClass());
                    handler.setMethod(method);
                    handler.setInstance(ncMap.get(key));
                    NC_BEANS.put(requestUrl + methodMapping.value(), handler);
                }
            }
        }
    }

    /**
     * 消息处理
     *
     * @param chatData
     * @param channel
     */
    public void handler(String chatData, Channel channel) {
        log.info("ChatDispatchServlet handler 消息内容{}", chatData);

        NCRequest ncRequest = JSONObject.parseObject(chatData, NCRequest.class);
        if (ncRequest == null || NCUtils.isNullOrEmpty(ncRequest.getPath())) {
            sendMsg(channel, "/404", NCResult.error("404", "not found"));
            return;
        }
        chatLoginUtils.handlerLogin(ncRequest);
        if (NC_BEANS.isEmpty()) {
            initNCBeans();
        }
        NCRequestHandler handler = NC_BEANS.get(ncRequest.getPath());
        if (handler == null) {
            sendMsg(channel, ncRequest.getPath(), NCResult.error("404", "not found"));
            return;
        }
        Type[] paramTypes = handler.getMethod().getGenericParameterTypes();
        Object[] params = new Object[paramTypes.length];
        try {
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].getTypeName().equals("io.netty.channel.Channel")) {
                    params[i] = channel;
                } else {
                    params[i] = JSONObject.parseObject(ncRequest.getData(), paramTypes[i]);
                }
            }
            Object resData = handler.getMethod().invoke(handler.getInstance(), params);
            if (resData != null) {
                if (log.isDebugEnabled()) log.debug("返回数据 {}", JSONObject.toJSONString(resData));
                sendMsg(channel, ncRequest.getPath(), resData);
            }
        } catch (IllegalAccessException e) {
            log.error("error ", e);
            sendMsg(channel, ncRequest.getPath(), NCResult.error());
        } catch (InvocationTargetException e) {
            log.error("error ", e);
            if (e.getTargetException() instanceof BaseException) {
                sendMsg(channel, ncRequest.getPath(), NCResult.error(((BaseException) e.getTargetException()).getCode(), e.getTargetException().getMessage()));
            } else {
                sendMsg(channel, ncRequest.getPath(), NCResult.error());
            }
        } catch (Exception e){
            log.error("error ", e);
            sendMsg(channel, ncRequest.getPath(), NCResult.error());
        }
    }

    /**
     * 校验是否登陆
     *
     * @param request
     * @return
     */
    private NCResult checkLogin(NCRequest request) {
        // 白名单数据不校验
        if (whiteUrl.contains(request.getPath())) {
            return NCResult.ok();
        }
        String accessToken = request.getAccessToken();
        String uuid = request.getUuid();
        if (accessToken == null) {
            return NCResult.error(ErrorEnum.LOGIN_TIME_OUT);
        }
        String tokenKey = RedisConstants.USER_LOGIN_INFO + accessToken;
        NCLoginUserVO loginUser = redisUtils.get(tokenKey, NCLoginUserVO.class);
        if (NCUtils.isNullOrEmpty(loginUser) || !loginUser.getUuid().equals(uuid)) {
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(accessToken);
//            NCResult<LoginUser> result = userClient.getUserInfo(loginVO);
//            if (result.getSuccess() && !result.getRows().isEmpty() && result.getRows().get(0) != null) {
//                loginUser = result.getRows().get(0);
//                redisUtils.set(tokenKey, loginUser, loginUser.getExpire());
//            } else {
//                return NCResult.error(ErrorEnum.LOGIN_TIME_OUT);
//            }
        }
        return NCResult.ok();
    }
}
