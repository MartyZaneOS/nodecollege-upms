package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: chat_debate
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-02-22 18:16:08
 */
@Data
public class ChatDebate {
    /**
     * 辩论id
     */
    private Long debateId;

    /**
     * 辩论类型
     * 1: 节点信息修改
     * 2: 节点链接上级
     * 3: 节点添加下级
     * 4: 节点删除下级
     * 5: 百科文章修改
     * 6: 自由讨论主题 original为正方观点，update为反方观点
     * 7: 节点题库新增修改删除
     */
    private Integer debateType;

    /**
     * 原始内容
     */
    private String originalJson;

    /**
     * 修改后内容
     */
    private String updateJson;

    /**
     * 申请人id
     */
    private Long userId;

    /**
     * 辩论结果 -1-发生错误，0-失败 1-成功 2-辩论中
     */
    private Integer result;

    /**
     * 结果说明
     */
    private String resultMsg;

    /**
     * 支持数
     */
    private Integer supportNum;

    /**
     * 反对数
     */
    private Integer refuseNum;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date endTime;
    /**
     * 随机数
     * 用户和worldTree进行数据确认
     */
    private String uuid;
}