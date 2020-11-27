package com.nodecollege.cloud.common.model.vo;

import com.nodecollege.cloud.common.model.po.OperateAppApi;
import lombok.Data;

import java.util.List;

/**
 * 接口controller VO
 *
 * @author LC
 * @date 2019/12/20 18:32
 */
@Data
public class ControllerVO {
    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 控制层名称
     */
    private String controllerName;

    /**
     * 接口列表
     */
    List<OperateAppApi> children;
}
