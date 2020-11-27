package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateTenant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsTenantMapper
 *
 * @author LC
 * @date 2019-12-06 23:56:14
 */
@Mapper
@Component
public interface OperateTenantMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateTenant record);

    /**
     * 根据主键id查询
     */
    OperateTenant selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateTenant record);

    /**
     * 查询租户列表
     */
    List<OperateTenant> selectTenantListByMap(Map<String, Object> javabean2Map);

    /**
     * 根据用户id查询租户列表
     * @param userId
     * @return
     */
    List<OperateTenant> selectTenantListByUserId(Long userId);
}