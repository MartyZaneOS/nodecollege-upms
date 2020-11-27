package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantMember;
import com.nodecollege.cloud.common.model.po.TenantMemberOrgRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * TenantMemberMapper
 *
 * @author LC
 * @date 2020-10-14 20:48:48
 */
@Mapper
@Component
public interface TenantMemberMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long memberId);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(TenantMember record);

    /**
     * 根据主键id查询
     */
    TenantMember selectByPrimaryKey(Long memberId);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(TenantMember record);

    List<TenantMember> selectMemberListByMap(Map<String, Object> queryMap);

    List<TenantMember> selectListByOrg(Map<String, Object> toMap);

    List<TenantMember> selectListByRoleOrg(TenantMemberOrgRole record);
}