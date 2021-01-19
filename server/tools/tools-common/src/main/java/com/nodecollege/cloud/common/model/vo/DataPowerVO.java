package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author LC
 * @date 2020/12/14 19:50
 */
@Data
public class DataPowerVO {
    // 数据权限
    private String dataPowerCode;
    // 数据用途 0-运营/运维，1-2C
    private Integer dataPowerUsage;
    // 用户id
    private Long userId;
    // 用户授权机构代码
    private List<String> orgCode;
}
