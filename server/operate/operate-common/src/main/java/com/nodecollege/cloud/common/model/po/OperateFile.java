package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: upms_file
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-03-21 12:08:09
 */
@Data
public class OperateFile {
    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 组织机构id
     */
    private Long orgId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     * 1-图片类
     * 2-文档类
     * 3-压缩文件类
     */
    private Integer fileType;

    /**
     * 文件用途
     * 0-用户头像
     * 1-聊天图片
     * 2-文章图片
     * 3-下载文件包
     * 4-文章md
     * 5-文章html
     */
    private Integer filePurpose;

    /**
     * 文件有效期
     * 单位 小时
     * 空-不删除
     */
    private Integer fileValidity;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    /**
     * 文件base64编码 不存数据库
     */
    private String fileBase64;
}