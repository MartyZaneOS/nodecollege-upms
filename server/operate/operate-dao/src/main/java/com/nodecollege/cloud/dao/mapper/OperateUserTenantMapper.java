package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateUserTenant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateUserTenantMapper
 *
 * @author LC
 * @date 2020-10-19 15:15:47
 */
@Mapper
@Component
public interface OperateUserTenantMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUserTenant record);

    /**
     * 根据主键id查询
     */
    OperateUserTenant selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateUserTenant record);

    List<OperateUserTenant> selectList(OperateUserTenant query);
}