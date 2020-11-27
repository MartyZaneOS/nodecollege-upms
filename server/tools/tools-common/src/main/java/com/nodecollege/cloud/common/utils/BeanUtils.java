package com.nodecollege.cloud.common.utils;

import com.nodecollege.cloud.common.exception.NCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {
    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    /**
     * map2javabean 把map对象转换为javabean
     *
     * @param map
     * @param beantype
     * @param <T>
     * @return
     */
    public static <T> T map2Bean(Map<String, Object> map, Class<T> beantype) {
        T object = null;
        try {
            object = beantype.newInstance();
            //获取类的属性描述器
            BeanInfo beaninfo = Introspector.getBeanInfo(beantype, Object.class);
            //获取类的属性集
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                //获取属性的名字
                String name = property.getName();
                //得到属性name在map中对应的value。
                Object value = map.get(name);
                //得到属性的set方法
                Method set = property.getWriteMethod();
                //接下来将map的value转换为属性的value //执行set方法
                set.invoke(object, value);
            }
        } catch (InstantiationException e) {
            logger.error("map2bean error", e);
            throw new NCException();
        } catch (IllegalAccessException e) {
            logger.error("map2bean error", e);
            throw new NCException();
        } catch (IntrospectionException e) {
            logger.error("map2bean error", e);
            throw new NCException();
        } catch (InvocationTargetException e) {
            logger.error("map2bean error", e);
            throw new NCException();
        }
        return object;
    }

    /**
     * 将javabean转换为map
     *
     * @param bean
     * @return
     */
    public static Map<String, Object> javabean2Map(Object bean) {//传入一个javabean对象
        Map<String, Object> map = new HashMap<>();
        try {
            //获取类的属性描述器
            BeanInfo beaninfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            //获取类的属性集
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                //得到属性的name
                String key = property.getName();
                Method get = property.getReadMethod();
                //执行get方法得到属性的值
                Object value = null;
                value = get.invoke(bean);
                map.put(key, value);
            }
        } catch (IntrospectionException e) {
            logger.error("javabean2map error", e);
            throw new NCException();
        } catch (IllegalAccessException e) {
            logger.error("javabean2map error", e);
            throw new NCException();
        } catch (InvocationTargetException e) {
            logger.error("javabean2map error", e);
            throw new NCException();
        }
        return map;
    }
}