package com.nodecollege.cloud.common.model;

import lombok.Data;

/**
 * @author LC
 * @date 2020/4/29 21:04
 */
@Data
public class NCResponse {
    /**
     * 路径
     */
    private String path;
    /**
     * 返回数据
     */
    private Object data;
}
