package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: upms_user
 * 版权：节点学院
 *
 * @author LC-ADMIN
 * @date 2019-12-03 23:25:09
 */
@Data
public class OperateUser {
    // 主键
    private Long userId;

    // 账号
    private String account;

    // 昵称
    private String nickname;

    // 邮箱
    private String email;

    // 电话
    private String telephone;

    // 用户头像
    private String avatar;

    // 用户头像缩略图
    private String avatarThumb;

    // 性别 0-男，1-女
    private Integer sex;

    // 出生日期
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date birthday;

    // 默认租户id
    private Long tenantId;

    // 默认登录租户代码
    private String tenantCode;

    // 首次登陆 0-首次登陆，1-非首次登陆
    private Integer firstLogin;

    //显示所有机构数据
    private Boolean showAllOrg;

    // 显示所有角色数据
    private Boolean showAllRole;

    // 默认机构代码 showAllOrg=false有效
    private String defaultOrgCode;

    // 默认角色代码 showAllRole=false有效
    private String defaultRoleCode;

    // 密码，密文
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // 密码盐值
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    // 状态 -1-已删除，0-不可删除，1-可删除, 2-冻结
    private Integer state;

    // 创建时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
}