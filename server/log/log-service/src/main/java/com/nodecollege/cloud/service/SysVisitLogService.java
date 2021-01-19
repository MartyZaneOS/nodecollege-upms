package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SysVisitLog;

import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/11/30 13:58
 */
public interface SysVisitLogService {

    // 获取当前统计数据
    List<SysVisitLog> getCurrentVisitData(QueryVO<SysVisitLog> query);

    // 获取历史访问数据
    List<SysVisitLog> getHistoryVisitData(QueryVO<SysVisitLog> query);

    // 统计分钟级访问数据
    Map<String, Integer> countVisitData(SysVisitLog visitLog);
}
