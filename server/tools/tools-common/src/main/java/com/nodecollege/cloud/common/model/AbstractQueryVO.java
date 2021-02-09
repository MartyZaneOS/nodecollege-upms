package com.nodecollege.cloud.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.utils.DateUtils;
import com.nodecollege.cloud.common.utils.NCStringUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2019/12/23 20:29
 */
@Data
public abstract class AbstractQueryVO extends AbstractBaseModel {

    /**
     * 分页
     */
    private Integer pageNum = 1;
    /**
     * 大小
     */
    private Integer pageSize = 10;
    /**
     * longList 通常当作主键list
     */
    private List<Long> longList;
    /**
     * 状态list
     */
    private List<Long> stateList;
    /**
     * orgList
     */
    private List<Long> orgList;
    /**
     * 字符串List
     */
    private List<String> stringList;
    /**
     * 开始日期
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date startDate;
    /**
     * 结束日期
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date endDate;
    /**
     * 开始时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = DateUtils.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS, timezone = NCConstants.TIME_ZONE.SHANGHAI)
    private Date endTime;
    /**
     * 排序方式 0-升序（ASC），1-降序（DESC）
     */
    private String sortDirection;
    /**
     * 排序列表
     */
    private String sortKey;
    /**
     * 分组关键字
     */
    private String groupByKey;

    /**
     * 是否需要排序
     *
     * @return
     */
    public Boolean isSort() {
        if (sortDirection == null || sortKey == null || StringUtils.isBlank(sortKey)) {
            return false;
        }
        if (NCConstants.ASC.equalsIgnoreCase(sortDirection)
                || NCConstants.DESC.equalsIgnoreCase(sortDirection)) {
            return true;
        }
        return false;
    }

    public String getGroupByKey() {
        return NCStringUtils.camelToUnderline(groupByKey, 1);
    }

    public String getSortKey() {
        return NCStringUtils.camelToUnderline(sortKey, 1);
    }
}
