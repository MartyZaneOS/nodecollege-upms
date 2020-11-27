package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * TenantRoleMapper
 *
 * @author LC
 * @date 2020-10-16 16:02:21
 */
@Mapper
@Component
public interface TenantRoleMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(TenantRole record);

    /**
     * 根据主键id查询
     */
    TenantRole selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(TenantRole record);

    List<TenantRole> selectListByMap(Map<String,Object> query);
}