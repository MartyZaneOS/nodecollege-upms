package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.util.List;

/**
 * 绑定关系vo
 *
 * @author LC
 * @date 2019/12/21 23:37
 */
@Data
public class BindVO extends AbstractBaseModel{
    /**
     * 决定方
     * true-以source为决定方
     * false-以target为决定方
     */
    private Boolean mainSource;
    /**
     * 绑定用途 0-运营/运维，1-2C
     */
    private Integer bindUsage;
    /**
     * 源主键列表
     */
    private List<Long> sourceIds;
    /**
     * 源代码列表
     */
    private List<String> sourceCodes;
    /**
     * 源全部绑定标志
     * true-全部绑定，从库中取所有进行绑定
     * false-部分绑定，取sourceIds中传的值进行绑定
     */
    private Boolean sourceAll = false;
    /**
     * 目标主键列表
     */
    private List<Long> targetIds;
    /**
     * 目标代码列表
     */
    private List<String> targetCodes;
    /**
     * 目标全部绑定标志
     * true-全部绑定，从库中取所有进行绑定
     * false-部分绑定，取targetIds中传的值进行绑定
     */
    private Boolean targetAll = false;
    /**
     * 应用id
     */
    private Long appId;
    /**
     * 操作人id
     */
    private Long userId;
    /**
     * 绑定状态 1-启用，2-禁用
     */
    private Integer state;
}
