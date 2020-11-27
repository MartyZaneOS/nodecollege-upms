package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SyncJob;
import com.nodecollege.cloud.common.model.po.SyncJobLog;
import com.nodecollege.cloud.service.SyncJobLogService;
import com.nodecollege.cloud.service.SyncJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/6/10 18:59
 */
@RestController
@RequestMapping("/sync")
public class SyncController {

    @Autowired
    private SyncJobService syncJobService;

    @Autowired
    private SyncJobLogService syncJobLogService;

    /**
     * 获取任务列表
     */
    @PostMapping("/getJobList")
    public NCResult<SyncJob> getJobList(@RequestBody QueryVO<SyncJob> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<SyncJob> list = syncJobService.getJobList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    /**
     * 获取任务class
     */
    @PostMapping("/getJobClassList")
    public NCResult<String> getJobClassList() {
        return NCResult.ok(syncJobService.getJobClassList());
    }

    /**
     * 添加任务
     */
    @RequestMapping("/addJob")
    public NCResult addJob(@RequestBody SyncJob syncJob) {
        syncJobService.addJob(syncJob);
        return NCResult.ok();
    }

    /**
     * 修改任务
     */
    @RequestMapping("/editJob")
    public NCResult editJob(@RequestBody SyncJob syncJob) {
        syncJobService.editJob(syncJob);
        return NCResult.ok();
    }

    /**
     * 删除任务
     */
    @RequestMapping("/delJob")
    public NCResult delJob(@RequestBody SyncJob syncJob) {
        syncJobService.delJob(syncJob);
        return NCResult.ok();
    }

    /**
     * 暂停/恢复任务
     */
    @RequestMapping("/pauseJob")
    public NCResult pauseJob(@RequestBody SyncJob syncJob) {
        syncJobService.pauseJob(syncJob);
        return NCResult.ok();
    }

    /**
     * 获取任务列表
     */
    @PostMapping("/getJobLogList")
    public NCResult<SyncJobLog> getJobLogList(@RequestBody QueryVO<SyncJobLog> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<SyncJobLog> list = syncJobLogService.getLogList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }
}
