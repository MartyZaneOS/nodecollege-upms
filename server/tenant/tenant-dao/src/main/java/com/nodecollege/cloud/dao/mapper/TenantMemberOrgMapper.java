package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantMemberOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * TenantMemberOrgMapper
 *
 * @author LC
 * @date 2020-10-15 21:53:33
 */
@Mapper
@Component
public interface TenantMemberOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(TenantMemberOrg record);

    /**
     * 根据主键id查询
     */
    TenantMemberOrg selectByPrimaryKey(Long id);

    List<TenantMemberOrg> selectList(TenantMemberOrg query);
}