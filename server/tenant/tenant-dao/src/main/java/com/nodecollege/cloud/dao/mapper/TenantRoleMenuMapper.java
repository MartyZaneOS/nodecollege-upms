package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.TenantRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * TenantRoleMenuMapper
 *
 * @author LC
 * @date 2020-10-15 21:57:03
 */
@Mapper
@Component
public interface TenantRoleMenuMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(TenantRoleMenu record);

    /**
     * 根据主键id查询
     */
    TenantRoleMenu selectByPrimaryKey(Integer id);

    List<TenantRoleMenu> selectListByMap(Map<String, Object> queryMap);
}