package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SysLog;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/8/10 20:15
 */
@RestController
    @RequestMapping("/log")
public class LogController {
    @Autowired
    private SysLogService sysLogService;

    @ApiAnnotation(modularName = "系统日志管理", description = "删除系统日志")
    @PostMapping("/delSysLog")
    public NCResult<Map<String, Integer>> delSysLog(@RequestBody Map<String, Integer> map) {
        Map<String, Integer> result = sysLogService.delSysLog(map);
        return NCResult.ok(result);
    }

    @ApiAnnotation(modularName = "系统日志管理", description = "查询系统日志")
    @PostMapping("/getSysLogList")
    public NCResult<SysLog> getSysLogList(@RequestBody QueryVO<SysLog> queryVO) {
        List<SysLog> list = new ArrayList<>();
        long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = sysLogService.getSysLogList(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = sysLogService.getSysLogList(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }
}
