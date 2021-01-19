package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SysLog;

import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/9/27 21:58
 */
public interface SysLogService {
    /**
     * 查询系统日志
     */
    List<SysLog> getSysLogList(QueryVO<SysLog> queryVO);

    /**
     * 删除系统日志
     */
    Map<String, Integer> delSysLog(Map<String, Integer> map);
}
