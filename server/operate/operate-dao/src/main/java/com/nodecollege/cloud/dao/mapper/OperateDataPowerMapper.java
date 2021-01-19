package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateDataPower;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateDataPowerMapper
 *
 * @author LC
 * @date 2020-12-14 18:01:09
 */
@Mapper
@Component
public interface OperateDataPowerMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateDataPower record);

    /**
     * 根据主键id查询
     */
    OperateDataPower selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateDataPower record);

    List<OperateDataPower> selectListByMap(Map<String, Object> queryMap);

    OperateDataPower selectOne(OperateDataPower query);
}