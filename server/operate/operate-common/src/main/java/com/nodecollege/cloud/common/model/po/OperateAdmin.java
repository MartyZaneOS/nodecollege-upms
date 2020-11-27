package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: upms_admin
 * 版权：节点学院
 *
 * @author LC
 * @date 2019-11-27 19:32:32
 */
@Data
public class OperateAdmin {
    /**
     * 主键
     */
    private Long adminId;
    /**
     * 账号
     */
    private String account;
    /**
     * 手机号
     */
    private String telephone;
    /**
     * 密码
     */
    private String password;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 是否首次登陆
     */
    private Boolean firstLogin;
    /**
     * 显示所有机构数据
     */
    private Boolean showAllOrg;
    /**
     * 显示所有角色数据
     */
    private Boolean showAllRole;
    /**
     * 默认机构代码 showAllOrg=false有效
     */
    private String defaultOrgCode;
    /**
     * 默认角色代码 showAllRole=false有效
     */
    private String defaultRoleCode;
    /**
     * 状态 -1-已删除，0-不可删除，1-正常，2-冻结
     */
    private Integer state;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
}