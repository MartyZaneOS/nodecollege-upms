package com.nodecollege.cloud.common.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Table: upms_user_password
 * 版权：节点学院
 *
 * @author LC-ADMIN
 * @date 2019-12-05 23:20:28
 */
@Data
public class OperateUserPassword {
    /**
     * Column: id
     */
    private Long userPwdId;

    /**
     * Column: user_id
     */
    private Long userId;

    /**
     * Column: password
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * Column: salt
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;


    /**
     * Column: 创建时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date createTime;
}