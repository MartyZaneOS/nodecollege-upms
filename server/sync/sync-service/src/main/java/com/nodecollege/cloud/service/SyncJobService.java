package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.SyncJob;

import java.util.List;
import java.util.Set;

/**
 * @author LC
 * @date 2020/6/10 19:16
 */
public interface SyncJobService {

    /**
     * 查询任务列表
     */
    List<SyncJob> getJobList(QueryVO<SyncJob> queryVO);

    /**
     * 获取所有任务执行class
     */
    Set<String> getJobClassList();

    /**
     * 添加任务
     */
    void addJob(SyncJob syncJob);

    /**
     * 修改任务
     **/
    void editJob(SyncJob appQuartz);

    /**
     * 删除任务
     **/
    void delJob(SyncJob appQuartz);

    /**
     * 暂停/恢复任务
     **/
    void pauseJob(SyncJob syncJob);
}
