package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.constants.QueueConstants;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SysLog;
import com.nodecollege.cloud.common.utils.QueueUtils;
import com.nodecollege.cloud.dao.mapper.SysLogMapper;
import com.nodecollege.cloud.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author LC
 * @date 2020/9/27 21:58
 */
@Slf4j
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private QueueUtils queueUtils;

    //    @Scheduled(cron = "*/30 * * * * ? ")
    @Override
    public Map<String, Integer> sysLogStorage() {
        long startTime = System.currentTimeMillis();
        log.info("定时消费日志信息！");
        long size = 0;
        int addSize = 0;
        while ((size = queueUtils.size(QueueConstants.SYS_LOG)) > 0) {
            log.info("待消费日志数量：{}", size);
            while (size > 0) {
                SysLog sysLog = queueUtils.popTask(QueueConstants.SYS_LOG, 500L, SysLog.class);
                if (sysLog != null) {
                    addSize++;
                    if (sysLog.getInParam() != null && sysLog.getInParam().length() > 8000){
                        sysLog.setInParam(sysLog.getInParam().substring(0, 8000));
                    }

                    if (sysLog.getOutParam() != null && sysLog.getOutParam().length() > 8000){
                        sysLog.setOutParam(sysLog.getOutParam().substring(0, 8000));
                    }
                    sysLogMapper.insertSelective(sysLog);
                }
                size--;
            }
        }
        log.info("入库：{}条，耗时：{}ms", addSize, System.currentTimeMillis() - startTime);
        Map<String, Integer> result = new HashMap<>();
        result.put("addSize", addSize);
        return result;
    }

    @Override
    public Map<String, Integer> delSysLog(Map<String, Integer> map) {
        // 获取保留天数
        Integer retentionDays = map.get("retentionDays");

        if (retentionDays == null){
            // 默认30天
            retentionDays = 30;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -retentionDays);

        QueryVO queryDelete = new QueryVO();
        queryDelete.setEndTime(calendar.getTime());
        Page page = PageHelper.startPage(1, 10);
        sysLogMapper.selectListByMap(queryDelete.toMap());
        long size = page.getTotal() > 100 ? page.getTotal() / 100 : 1;
        for (int i = 1; i < size; i++) {
            queryDelete.setPageNum(i);
            queryDelete.setPageSize(100);
            delSysLog(queryDelete);
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("delSize", Integer.parseInt(page.getTotal() + ""));
        return result;
    }

    private void delSysLog(QueryVO queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<SysLog> delList = sysLogMapper.selectListByMap(queryVO.toMap());

        for (SysLog log : delList){
            sysLogMapper.deleteByPrimaryKey(log.getId());
        }
    }

    @Override
    public List<SysLog> getSysLogList(QueryVO<SysLog> queryVO) {
        return sysLogMapper.selectListByMap(queryVO.toMap());
    }
}
