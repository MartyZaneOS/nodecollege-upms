package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantMemberOrgRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * TenantMemberOrgRoleMapper
 *
 * @author LC
 * @date 2020-10-15 21:53:56
 */
@Mapper
@Component
public interface TenantMemberOrgRoleMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(TenantMemberOrgRole record);

    /**
     * 根据主键id查询
     */
    TenantMemberOrgRole selectByPrimaryKey(Long id);

    List<TenantMemberOrgRole> selectList(TenantMemberOrgRole record);
}