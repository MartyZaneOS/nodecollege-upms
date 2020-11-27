package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAppApi;
import com.nodecollege.cloud.common.model.vo.InterfaceTreeVO;

import java.util.List;

/**
 * @author LC
 * @date 2019/12/20 18:41
 */
public interface AppApiService {


    List<OperateAppApi>  getAppApiList(QueryVO<OperateAppApi> queryVO);
    /**
     * 查询接口List缓存
     */
    List<OperateAppApi> getApiListCache();

    /**
     * 查询接口树
     *
     * @return
     */
    List<InterfaceTreeVO> getApiTree();

    /**
     * 获取 已经用注解标注的接口信息
     * 注解标记的在数据库中不存在，则将记录插入数据库
     */
    List<OperateAppApi> initApiList(List<OperateAppApi> annList);

    /**
     * 清除接口map缓存
     */
    void clearApiListCache();

    /**
     * 删除接口
     */
    void delApi(OperateAppApi upmsApi);

    /**
     * 更新所有接口授权信息
     */
    void syncAllAppApiAccessAuth();

    /**
     * 更新接口授权信息
     * @param appApi
     */
    void updateAppApiAccessAuth(OperateAppApi appApi);
}
