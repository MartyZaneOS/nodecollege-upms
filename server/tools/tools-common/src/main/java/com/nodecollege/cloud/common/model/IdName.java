package com.nodecollege.cloud.common.model;

import lombok.Data;

/**
 * @author LC
 * @date 2019/12/3 19:00
 */
@Data
public class IdName {
    /**
     * 主键
     */
    private Long id;
    /**
     * 名称
     */
    private String name;

    public IdName(){}

    public IdName(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
