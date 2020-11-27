package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author LC
 * @date 2020/4/29 20:28
 */
@Data
public class NCRequestHandler {
    //映射的路径
    public String path;
    //类对象
    public Class clazz;
    //方法对象
    public Method method;
    //类的实例对象
    public Object instance;

    public Class requestBodyClass;
}
