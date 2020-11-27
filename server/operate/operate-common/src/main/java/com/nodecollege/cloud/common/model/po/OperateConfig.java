package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: upms_config
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-02-07 23:10:21
 */
@Data
public class OperateConfig {
    // 配置主键
    private Long configId;

    // 预制配置标志，0或空-非预制配置，1-预制配置
    private Integer preFlag;

    /**
     * 配置用途
     * 0-系统配置
     * 1-管理员机构配置，2-管理员配置，3-管理员机构-管理员配置
     * 4-用户机构配置，5-用户配置，6-用户机构-用户配置
     * 7-租户配置，8-租户-机构配置，9-租户-机构-成员配置，10-租户-成员配置
     */
    private Integer configUsage;

    // 配置分组
    private String configGroup;

    // 配置代码
    private String configCode;

    // 配置名称
    private String configName;

    // 配置值
    private String configValue;

    // 配置描述
    private String configExplain;

    /**
     * 配置类型
     * 0-输入框
     * 1-下拉单选
     * 2-下拉多选
     */
    private Integer configType;

    // 选项列表 配置类型为2、3时有效
    private String optionList;

    // 配置状态 0-不可编辑删除 1-正常
    private Integer state;

    // 管理员机构代码 配置类型为1、3必填
    private String adminOrgCode;

    // 管理员账号 配置类型为2、3必填
    private String adminAccount;

    // 用户机构代码 配置类型为4、6必填
    private String userOrgCode;

    // 用户账号 配置类型为5、6必填
    private String userAccount;

    // 租户代码 配置类型为7、8、9、10是必填
    private String tenantCode;

    // 租户机构代码 配置类型为8、9必填
    private String tenantOrgCode;

    // 租户成员账号 配置类型为9、10必填
    private String memberAccount;

    // 创建用户
    private String createUser;

    // 创建时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    // 更新用户
    private String updateUser;

    // 更新时间
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date updateTime;

    // 租户id，查询租户配置时使用，不存库
    private Long tenantId;
}