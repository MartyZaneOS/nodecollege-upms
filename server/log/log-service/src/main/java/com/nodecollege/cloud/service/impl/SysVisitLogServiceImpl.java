package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SysVisitLog;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.SysVisitLogMapper;
import com.nodecollege.cloud.service.SysVisitLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LC
 * @date 2020/11/30 13:58
 */
@Slf4j
@Service
public class SysVisitLogServiceImpl implements SysVisitLogService {

    @Autowired
    private SysVisitLogMapper sysVisitLogMapper;

    @Override
    public List<SysVisitLog> getCurrentVisitData(QueryVO<SysVisitLog> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getVisitType());
        NCUtils.nullOrEmptyThrow(queryVO.getData().getTimeDimension());
        NCUtils.nullOrEmptyThrow(queryVO.getData().getVisitAppName());
        if (queryVO.getStartTime() == null || queryVO.getEndTime() == null) {
            // 当前日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String current = sdf.format(new Date());

            // 当前时间
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                queryVO.setStartTime(sdf.parse(current + "000000"));
                queryVO.setEndTime(sdf.parse(current + "235959"));
            } catch (ParseException e) {
                log.error("日期格式有误！{}", current);
                throw new NCException("-1", "日期格式有误！");
            }
        }

        int visitType = queryVO.getData().getVisitType();
        int timeDimension = queryVO.getData().getTimeDimension();
        if (visitType == 0) {
            if (timeDimension == 0) {
                return sysVisitLogMapper.countAppUrlMinuteVisit(queryVO.toMap());
            }
            if (timeDimension == 1) {
                return sysVisitLogMapper.countAppUrlHourVisit(queryVO.toMap());
            }
            if (timeDimension == 2) {
                return sysVisitLogMapper.countAppUrlDayVisit(queryVO.toMap());
            }
        } else if (visitType == 1) {
            if (timeDimension == 0) {
                return sysVisitLogMapper.countAppMinuteVisit(queryVO.toMap());
            }
            if (timeDimension == 1) {
                return sysVisitLogMapper.countAppHourVisit(queryVO.toMap());
            }
            if (timeDimension == 2) {
                return sysVisitLogMapper.countAppDayVisit(queryVO.toMap());
            }
        } else if (visitType == 2) {
            if (timeDimension == 0) {
                return sysVisitLogMapper.countIpUrlMinuteVisit(queryVO.toMap());
            }
            if (timeDimension == 1) {
                return sysVisitLogMapper.countIpUrlHourVisit(queryVO.toMap());
            }
            if (timeDimension == 2) {
                return sysVisitLogMapper.countIpUrlDayVisit(queryVO.toMap());
            }
        } else if (visitType == 3) {
            if (timeDimension == 0) {
                return sysVisitLogMapper.countIpMinuteVisit(queryVO.toMap());
            }
            if (timeDimension == 1) {
                return sysVisitLogMapper.countIpHourVisit(queryVO.toMap());
            }
            if (timeDimension == 2) {
                return sysVisitLogMapper.countIpDayVisit(queryVO.toMap());
            }
        } else if (visitType == 4) {
            if (timeDimension == 1) {
                return sysVisitLogMapper.countArticleHourVisit(queryVO.toMap());
            }
        } else {
            throw new NCException("-1", "访问类型错误！");
        }
        throw new NCException("-1", "时间维度错误！");
    }

    @Override
    public List<SysVisitLog> getHistoryVisitData(QueryVO<SysVisitLog> query) {
        NCUtils.nullOrEmptyThrow(query.getStartDate());
        NCUtils.nullOrEmptyThrow(query.getEndDate());
        NCUtils.nullOrEmptyThrow(query.getData().getVisitType());
        NCUtils.nullOrEmptyThrow(query.getData().getTimeDimension());
        return sysVisitLogMapper.selectListByMap(query.toMap());
    }

    @Override
    public Map<String, Integer> countVisitData(SysVisitLog visitLog) {
        if (visitLog.getVisitDay() == null) {
            // 日期为空时，取上一天的数据
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, -1);
            Date end = c.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            visitLog.setVisitDay(Integer.valueOf(sdf.format(end)));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        QueryVO queryVO = new QueryVO();
        try {
            queryVO.setStartTime(sdf.parse(visitLog.getVisitDay() + "000000"));
            queryVO.setEndTime(sdf.parse(visitLog.getVisitDay() + "235959"));
        } catch (ParseException e) {
            log.error("日期格式有误！{}", visitLog.getVisitDay());
            throw new NCException("-1", "日期格式有误！");
        }
        if (queryVO.getEndTime().compareTo(new Date()) > 0) {
            throw new NCException("-1", "统计日期不能大于当前日期！");
        }
        // 查询微服务接口访问数据
//        List<SysVisitLog> minuteList = sysVisitLogMapper.countAppUrlMinuteVisit(queryVO.toMap());
        List<SysVisitLog> hourList = sysVisitLogMapper.countAppUrlHourVisit(queryVO.toMap());
        List<SysVisitLog> dayList = sysVisitLogMapper.countAppUrlDayVisit(queryVO.toMap());

        // 查询微服务访问数据
//        List<SysVisitLog> appMinuteList = sysVisitLogMapper.countAppMinuteVisit(queryVO.toMap());
        List<SysVisitLog> appHourList = sysVisitLogMapper.countAppHourVisit(queryVO.toMap());
        List<SysVisitLog> appDayList = sysVisitLogMapper.countAppDayVisit(queryVO.toMap());

        // 查询ip接口访问数据
//        List<SysVisitLog> ipUrlMinuteList = sysVisitLogMapper.countIpUrlMinuteVisit(queryVO.toMap());
        List<SysVisitLog> ipUrlHourList = sysVisitLogMapper.countIpUrlHourVisit(queryVO.toMap());
        List<SysVisitLog> ipUrlDayList = sysVisitLogMapper.countIpUrlDayVisit(queryVO.toMap());

        // 查询ip访问数据
//        List<SysVisitLog> ipMinuteList = sysVisitLogMapper.countIpMinuteVisit(queryVO.toMap());
        List<SysVisitLog> ipHourList = sysVisitLogMapper.countIpHourVisit(queryVO.toMap());
        List<SysVisitLog> ipDayList = sysVisitLogMapper.countIpDayVisit(queryVO.toMap());

        // 查询文章详情访问数据
        List<SysVisitLog> articleHourList = sysVisitLogMapper.countArticleHourVisit(queryVO.toMap());

        List<SysVisitLog> all = new ArrayList<>();
//        all.addAll(minuteList);
        all.addAll(hourList);
        all.addAll(dayList);
//        all.addAll(appMinuteList);
        all.addAll(appHourList);
        all.addAll(appDayList);

//        all.addAll(ipUrlMinuteList);
        all.addAll(ipUrlHourList);
        all.addAll(ipUrlDayList);
//        all.addAll(ipMinuteList);
        all.addAll(ipHourList);
        all.addAll(ipDayList);

        all.addAll(articleHourList);

        // 删除已有的统计数据
        SysVisitLog del = new SysVisitLog();
        del.setVisitDay(visitLog.getVisitDay());
        int delSize = sysVisitLogMapper.deleteByParam(del);

        // 插入统计数据
        for (int i = 0; i < all.size(); i++) {
            all.get(i).setCreateTime(new Date());
            sysVisitLogMapper.insertSelective(all.get(i));
        }
        Map<String, Integer> resMap = new HashMap<>();
        resMap.put("delSize", delSize);
        resMap.put("addSize", all.size());
        resMap.put("visitDay", visitLog.getVisitDay());
        return resMap;
    }
}
