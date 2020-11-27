package com.nodecollege.cloud.plugins;

/**
 *
 * @author LC
 * @date 17:54 2019/11/2
 **/
public enum MyBatisAnnotationEnum {

    /**
     * lombok注解
     */
    DATA("@Data", "lombok.Data"),

    Mapper("@Mapper", "org.apache.ibatis.annotations.Mapper"),
    /**
     * 增加支持，防止ide检查报错
     */
    Component("@Component", "org.springframework.stereotype.Component"),

    Param("@Param", "org.apache.ibatis.annotations.Param"),

    ApiModel("@ApiModel", "io.swagger.annotations.ApiModel"),

    ApiModelProperty("@ApiModelProperty", "io.swagger.annotations.ApiModelProperty"),

    JsonFormat("@JsonFormat", "com.fasterxml.jackson.annotation.JsonFormat");

    private String annotation;

    private String clazz;

    MyBatisAnnotationEnum(String annotation, String clazz) {
        this.annotation = annotation;
        this.clazz = clazz;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getClazz() {
        return clazz;
    }

}
