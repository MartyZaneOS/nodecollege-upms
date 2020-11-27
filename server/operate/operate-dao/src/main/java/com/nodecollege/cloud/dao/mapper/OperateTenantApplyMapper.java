package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateTenantApply;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateTenantApplyMapper
 *
 * @author LC
 * @date 2020-08-28 20:08:31
 */
@Mapper
@Component
public interface OperateTenantApplyMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateTenantApply record);

    /**
     * 根据主键id查询
     */
    OperateTenantApply selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateTenantApply record);

    List<OperateTenantApply> selectListByMap(Map<String, Object> queryMap);
}