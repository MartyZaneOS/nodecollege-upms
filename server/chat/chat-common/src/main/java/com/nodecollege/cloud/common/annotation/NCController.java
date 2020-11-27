package com.nodecollege.cloud.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 自定义RequestMapping
 * 用于netty消息转发
 *
 * @author LC
 * @date 11:29 2020/4/25
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NCController {

    /**
     * 校验数据权限
     */
    boolean checkDataPower() default false;

}
