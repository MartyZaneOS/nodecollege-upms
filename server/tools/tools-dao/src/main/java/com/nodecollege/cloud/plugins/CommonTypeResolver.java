package com.nodecollege.cloud.plugins;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

/**
 * 类型转换
 *
 * @author LC
 * @date 17:54 2019/11/2
 **/
public class CommonTypeResolver extends JavaTypeResolverDefaultImpl {
    /**
     * 将tinyint转换为Integer，这里是关键所在
     */
    public CommonTypeResolver() {
        super();
        super.typeMap.put(-6, new JdbcTypeInformation("TINYINT",
                new FullyQualifiedJavaType(Integer.class.getName())));
    }
}
