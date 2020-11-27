package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantRoleOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * TenantRoleOrgMapper
 *
 * @author LC
 * @date 2020-10-16 16:05:03
 */
@Mapper
@Component
public interface TenantRoleOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(TenantRoleOrg record);

    /**
     * 根据主键id查询
     */
    TenantRoleOrg selectByPrimaryKey(Long id);

    /**
     * 修改数据
     */
    int updateByPrimaryKey(TenantRoleOrg record);

    List<TenantRoleOrg> selectList(TenantRoleOrg query);
}