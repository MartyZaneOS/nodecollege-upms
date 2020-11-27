package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SyncJobLog;

import java.util.List;

/**
 * @author LC
 * @date 2020/6/12 18:05
 */
public interface SyncJobLogService {

    /**
     * 保存日志
     */
    void saveLog(SyncJobLog jobLog);

    /**
     * 查询日志
     */
    List<SyncJobLog> getLogList(QueryVO<SyncJobLog> queryVO);

    /**
     * 删除日志
     */
    void delLog(SyncJobLog jobLog);
}
