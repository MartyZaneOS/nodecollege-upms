package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * TenantOrgMapper
 *
 * @author LC
 * @date 2020-10-16 16:00:03
 */
@Mapper
@Component
public interface TenantOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(TenantOrg record);

    /**
     * 根据主键id查询
     */
    TenantOrg selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(TenantOrg record);

    List<TenantOrg> selectListByMap(Map<String, Object> queryMap);

    List<TenantOrg> selectListByRole(Map<String, Object> queryMap);

    List<TenantOrg> selectListByMember(Map<String, Object> queryMap);
}