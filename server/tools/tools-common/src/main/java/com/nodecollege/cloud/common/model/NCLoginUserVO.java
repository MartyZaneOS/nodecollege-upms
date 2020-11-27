package com.nodecollege.cloud.common.model;

import lombok.Data;

/**
 * 登录用户信息
 *
 * @author LC
 * @date 2020/9/16 22:06
 */
@Data
public class NCLoginUserVO extends PowerVO {
    /**
     * 登录用户id
     */
    private Long loginId;
    /**
     * 用户账号
     */
    private String account;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户头像缩略图
     */
    private String avatarThumb;
    /**
     * 租户id 租户成员登录时有效
     */
    private Long tenantId;
    /**
     * 租户代码 租户成员登录时有效
     */
    private String tenantCode;
    /**
     * 租户名称 租户成员登录时有效
     */
    private String tenantName;
    /**
     * 登录token
     */
    private String accessToken;
    /**
     * 信息随机值
     */
    private String uuid;
    /**
     * 登陆过期时间
     */
    private Integer expire;

    public NCLoginUserVO() {}

    public NCLoginUserVO(PowerVO powerVO) {
        this.setShowAllOrg(powerVO.getShowAllOrg());
        this.setShowAllRole(powerVO.getShowAllRole());
        this.setDefaultOrgCode(powerVO.getDefaultOrgCode());
        this.setDefaultRoleCode(powerVO.getDefaultRoleCode());
        this.setOrgList(powerVO.getOrgList());
        this.setRoleList(powerVO.getRoleList());
        this.setOrgRoleMap(powerVO.getOrgRoleMap());
        this.setRoleOrgMap(powerVO.getRoleOrgMap());
        this.setMenuTree(powerVO.getMenuTree());
    }
}
