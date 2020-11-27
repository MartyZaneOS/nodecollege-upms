package com.nodecollege.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * 自定义Controller
 * 用于netty消息转发
 *
 * @author LC
 * @date 11:29 2020/4/25
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NCRequestMapping {

    /**
     * 校验数据权限
     */
    String value() default "";

}
