package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateDataPower;
import com.nodecollege.cloud.common.model.po.OperateDataPowerAuth;
import com.nodecollege.cloud.common.model.vo.DataPowerVO;

import java.util.List;
import java.util.Map;

/**
 * @author LC
 * @date 2020/12/14 18:13
 */
public interface DataPowerService {
    /**
     * 查询数据权限列表
     */
    List<OperateDataPower> getList(QueryVO<OperateDataPower> queryVO);

    /**
     * 添加数据权限
     */
    void addDataPower(OperateDataPower dataPower);

    /**
     * 编辑数据权限
     */
    void editDataPower(OperateDataPower dataPower);

    /**
     * 查询授权数据列表
     */
    List<OperateDataPowerAuth> getAuthList(QueryVO<OperateDataPower> queryVO);

    /**
     * 添加授权
     */
    void addAuth(OperateDataPowerAuth auth);

    /**
     * 编辑授权
     */
    void editAuth(OperateDataPowerAuth auth);

    /**
     * 删除授权
     */
    void delAuth(OperateDataPowerAuth auth);

    /**
     * 获取用户授权信息
     */
    List<String> getUserAuth(DataPowerVO dataPowerVO);
}
