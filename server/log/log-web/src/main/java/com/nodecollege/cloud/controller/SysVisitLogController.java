package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SysVisitLog;
import com.nodecollege.cloud.service.SysVisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/11/30 14:01
 */
@RestController
@RequestMapping("/sysVisit")
public class SysVisitLogController {

    @Autowired
    private SysVisitLogService sysVisitLogService;

    @ApiAnnotation(modularName = "系统访问统计", description = "查询当前统计数据")
    @PostMapping("/getCurrentVisitData")
    public NCResult<SysVisitLog> getCurrentVisitData(@RequestBody QueryVO<SysVisitLog> query) {
        return NCResult.ok(sysVisitLogService.getCurrentVisitData(query));
    }

    @ApiAnnotation(modularName = "系统访问统计", description = "查询当前统计数据")
    @PostMapping("/getHistoryVisitData")
    public NCResult<SysVisitLog> getHistoryVisitData(@RequestBody QueryVO<SysVisitLog> query) {
        List<SysVisitLog> list = sysVisitLogService.getHistoryVisitData(query);
        return NCResult.ok(list);
    }

    @ApiAnnotation(modularName = "系统访问统计", description = "统计访问数据")
    @PostMapping("/countVisitData")
    public NCResult<Map<String, Integer>> countVisitData(@RequestBody SysVisitLog visitLog) {
        Map<String, Integer> res = sysVisitLogService.countVisitData(visitLog);
        return NCResult.ok(res);
    }
}
