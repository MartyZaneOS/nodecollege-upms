package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 接口树VO
 * @author LC
 * @date 2019/12/20 18:30
 */
@Data
public class InterfaceTreeVO {

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 控制层列表
     */
    private List<ControllerVO> children;
}
