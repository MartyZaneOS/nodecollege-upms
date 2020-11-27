package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * 邀请实体
 * Table: upms_user_invitation
 * 版权：节点学院
 *
 * @author LC
 * @date 2019-12-12 15:56:48
 */
@Data
public class OperateUserInvite {
    /**
     * 主键
     */
    private Long id;

    /**
     * 被邀请用户电话
     */
    private String telephone;

    /**
     * 被邀请用户名称
     */
    private String userName;

    /**
     * 邀请租户id
     */
    private Long tenantId;

    // 邀请状态 0-邀请中，1-邀请成功
    private Integer state;

    // 发出邀请人姓名
    private String createUser;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;
}