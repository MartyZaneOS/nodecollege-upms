package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateAdminRoleMapper
 *
 * @author LC
 * @date 2020-09-07 20:55:04
 */
@Mapper
@Component
public interface OperateAdminOrgRoleMapper {
    /**
     * 根据主键删除数据
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     * @param record OperateAdminRole
     * @return int
     */
    int insert(OperateAdminOrgRole record);

    /**
     * 根据主键id查询
     * @param id Integer
     * @return OperateAdminRole
     */
    OperateAdminOrgRole selectByPrimaryKey(Long id);

    /**
     * 修改数据
     * @param record OperateAdminRole
     * @return int
     */
    int updateByPrimaryKey(OperateAdminOrgRole record);

    /**
     * 查询管理员角色
     */
    List<OperateAdminOrgRole> selectList(OperateAdminOrgRole record);
}