package com.nodecollege.cloud.common.model.po;

import lombok.Data;

/**
 * Table: o_user_org
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-09-30 20:54:11
 */
@Data
public class OperateUserOrg {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 机构代码
     */
    private String orgCode;
}