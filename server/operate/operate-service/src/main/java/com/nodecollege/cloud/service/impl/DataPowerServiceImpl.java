package com.nodecollege.cloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateDataPower;
import com.nodecollege.cloud.common.model.po.OperateDataPowerAuth;
import com.nodecollege.cloud.common.model.vo.DataPowerVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateDataPowerAuthMapper;
import com.nodecollege.cloud.dao.mapper.OperateDataPowerMapper;
import com.nodecollege.cloud.service.DataPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author LC
 * @date 2020/12/14 18:14
 */
@Service
public class DataPowerServiceImpl implements DataPowerService {

    @Autowired
    private OperateDataPowerMapper dataPowerMapper;

    @Autowired
    private OperateDataPowerAuthMapper dataPowerAuthMapper;

    @Override
    public List<OperateDataPower> getList(QueryVO<OperateDataPower> queryVO) {
        return dataPowerMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void addDataPower(OperateDataPower dataPower) {
        NCUtils.nullOrEmptyThrow(dataPower.getDataPowerUsage());
        NCUtils.nullOrEmptyThrow(dataPower.getDataPowerCode());
        NCUtils.nullOrEmptyThrow(dataPower.getDataPowerName());
        NCUtils.nullOrEmptyThrow(dataPower.getDataPowerType());

        OperateDataPower queryVO = new OperateDataPower();
        queryVO.setDataPowerCode(dataPower.getDataPowerCode());
        OperateDataPower ex = dataPowerMapper.selectOne(queryVO);
        if (ex != null) {
            throw new NCException("", "数据权限代码已存在！");
        }

        dataPowerMapper.insertSelective(dataPower);
    }

    @Override
    public void editDataPower(OperateDataPower dataPower) {
        NCUtils.nullOrEmptyThrow(dataPower.getId());
        OperateDataPower ex = dataPowerMapper.selectByPrimaryKey(dataPower.getId());
        NCUtils.nullOrEmptyThrow(ex);

        OperateDataPower update = new OperateDataPower();
        update.setId(ex.getId());
        update.setDataPowerName(dataPower.getDataPowerName());
        update.setDataOption(dataPower.getDataOption());
        dataPowerMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    public List<OperateDataPowerAuth> getAuthList(QueryVO<OperateDataPower> queryVO) {
        return dataPowerAuthMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void addAuth(OperateDataPowerAuth auth) {
        NCUtils.nullOrEmptyThrow(auth.getDataPowerCode());
        NCUtils.nullOrEmptyThrow(auth.getOrgCode());

        OperateDataPower queryVO = new OperateDataPower();
        queryVO.setDataPowerCode(auth.getDataPowerCode());
        OperateDataPower ex = dataPowerMapper.selectOne(queryVO);
        NCUtils.nullOrEmptyThrow(ex, "", "数据权限不存在！");

        if (ex.getDataPowerType() == 1) {
            NCUtils.nullOrEmptyThrow(auth.getUserId());
        }

        auth.setCreateTime(new Date());
        auth.setState(null);
        dataPowerAuthMapper.insertSelective(auth);
    }

    @Override
    public void editAuth(OperateDataPowerAuth auth) {
        NCUtils.nullOrEmptyThrow(auth.getId());
        NCUtils.nullOrEmptyThrow(auth.getDataPowerCode());
        OperateDataPowerAuth ex = dataPowerAuthMapper.selectByPrimaryKey(auth.getId());
        NCUtils.nullOrEmptyThrow(ex);

        if (!ex.getDataPowerCode().equals(auth.getDataPowerCode())) {
            throw new NCException("", "无权修改！");
        }

        if (ex.getState() != null && ex.getState() == 0) {
            throw new NCException("", "该数据不能修改！");
        }

        OperateDataPowerAuth update = new OperateDataPowerAuth();
        update.setId(ex.getId());
        update.setAllData(auth.getAllData());
        update.setAuthList(auth.getAuthList());
        dataPowerAuthMapper.updateByPrimaryKeySelective(update);
    }

    @Override
    public void delAuth(OperateDataPowerAuth auth) {
        NCUtils.nullOrEmptyThrow(auth.getId());
        NCUtils.nullOrEmptyThrow(auth.getDataPowerCode());
        OperateDataPowerAuth ex = dataPowerAuthMapper.selectByPrimaryKey(auth.getId());

        if (!ex.getDataPowerCode().equals(auth.getDataPowerCode())) {
            throw new NCException("", "无权修改！");
        }

        if (ex.getState() != null && ex.getState() == 0) {
            throw new NCException("", "该数据不能删除！");
        }
        dataPowerAuthMapper.deleteByPrimaryKey(auth.getId());
    }

    @Override
    public List<String> getUserAuth(DataPowerVO dataPowerVO) {
        NCUtils.nullOrEmptyThrow(dataPowerVO.getDataPowerCode());
        NCUtils.nullOrEmptyThrow(dataPowerVO.getUserId());

        OperateDataPower query = new OperateDataPower();
        query.setDataPowerCode(dataPowerVO.getDataPowerCode());
        OperateDataPower ex = dataPowerMapper.selectOne(query);
        NCUtils.nullOrEmptyThrow(ex);

        OperateDataPowerAuth queryAuth = new OperateDataPowerAuth();
        queryAuth.setDataPowerCode(dataPowerVO.getDataPowerCode());
        if (ex.getDataPowerType() == 1) {
            queryAuth.setUserId(dataPowerVO.getUserId());
        }
        QueryVO<OperateDataPowerAuth> queryVO = new QueryVO<>(queryAuth);
        queryVO.setStringList(dataPowerVO.getOrgCode());
        List<OperateDataPowerAuth> exAuth = dataPowerAuthMapper.selectListByMap(queryVO.toMap());
        List<String> authList = new ArrayList<>();
        for (OperateDataPowerAuth auth : exAuth) {
            if (auth.getAllData() != null && auth.getAllData()) {
                // 授权所有，直接返回空
                return new ArrayList<>();
            }
            authList.addAll(JSONArray.parseArray(auth.getAuthList(), String.class));
        }
        if (authList.isEmpty()) {
            // 不是授权所有，也没具体的授权信息，返回null 表示无权限查询操作
            authList = null;
        }
        return authList;
    }
}
