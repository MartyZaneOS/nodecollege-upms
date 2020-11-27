package com.nodecollege.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * api注解
 *
 * @author LC
 * @date 20:09 2019/12/21
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAnnotation {
    /**
     * 模块名称
     */
    String modularName() default "";

    /**
     * 模块描述
     */
    String description() default "";

    /**
     * 状态
     * 1- 正常
     * 2- 后期删除
     */
    int state() default 1;

    /**
     * 访问来源
     * 1-不校验，任何来源都能调用，如果加了权限校验，会进行权限校验
     * 2-只能内部调用 来源为nc-ngxin的调用会被拦截
     * 3-只能外部调用，来源只能为nc-nginx
     */
    int accessSource() default 1;
}
