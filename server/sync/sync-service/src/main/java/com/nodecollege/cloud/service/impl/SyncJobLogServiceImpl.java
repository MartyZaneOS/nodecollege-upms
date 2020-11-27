package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SyncJobLog;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.SyncJobLogMapper;
import com.nodecollege.cloud.service.SyncJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2020/6/12 21:44
 */
@Service
public class SyncJobLogServiceImpl implements SyncJobLogService {

    @Autowired
    private SyncJobLogMapper syncJobLogMapper;

    @Override
    public void saveLog(SyncJobLog jobLog) {
        NCUtils.nullOrEmptyThrow(jobLog.getJobId(), "-1", "任务id不能为空！");
        NCUtils.nullOrEmptyThrow(jobLog.getLogState(), "-1", "日志状态不能为空！");
        NCUtils.nullOrEmptyThrow(jobLog.getLogMessage(), "-1", "日志内容不能为空！");
        jobLog.setCreateTime(new Date());
        syncJobLogMapper.insertSelective(jobLog);
    }

    @Override
    public List<SyncJobLog> getLogList(QueryVO<SyncJobLog> queryVO) {
        return syncJobLogMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void delLog(SyncJobLog jobLog) {
        NCUtils.nullOrEmptyThrow(jobLog.getJobLogId(), "-1", "任务日志id不能为空！");
        syncJobLogMapper.deleteByPrimaryKey(jobLog.getJobLogId());
    }
}
