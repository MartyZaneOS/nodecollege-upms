package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateDataPower;
import com.nodecollege.cloud.common.model.po.OperateDataPowerAuth;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateDataPowerAuthMapper
 *
 * @author LC
 * @date 2020-12-14 18:06:32
 */
@Mapper
@Component
public interface OperateDataPowerAuthMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateDataPowerAuth record);

    /**
     * 根据主键id查询
     */
    OperateDataPowerAuth selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateDataPowerAuth record);

    List<OperateDataPowerAuth> selectListByMap(Map<String, Object> queryMap);
}