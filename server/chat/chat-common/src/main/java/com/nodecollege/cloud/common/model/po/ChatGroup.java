package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: chat_group
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-02-16 00:19:51
 */
@Data
public class ChatGroup {
    /**
     * 群组id
     */
    private Long groupId;

    /**
     * 群编号
     */
    private Long groupNo;

    /**
     * 群组名称
     */
    private String groupName;

    /**
     * 群组公告
     */
    private String notice;

    /**
     * 群组公告设置
     * 1-所有人都可以修改
     * 2-管理员可以修改
     * 3-只有群主可以修改
     */
    private Integer noticeSetting;

    /**
     * 群组设置
     * 1-成员随意发言
     * 2-管理员可以发言
     * 3-只有群主可以发言
     */
    private Integer setting;

    /**
     * 群组类型
     * 1-公司全员群
     * 2-公司部门群
     * 3-公司内部群
     * 4-公司外部群
     * 5-辩论群
     */
    private Integer groupType;

    /**
     * 租户id
     * 处理类型为公司外部群外，其他都必填
     */
    private Long tenantId;

    /**
     * 组织机构id
     * 公司部门群必填
     */
    private Long orgId;

    /**
     * 辩论主键
     * 辩论群必填
     */
    private Long debateId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date updateTime;

    /**
     * 状态
     * 1-正常
     * -1-已解散
     */
    private Integer state;
}