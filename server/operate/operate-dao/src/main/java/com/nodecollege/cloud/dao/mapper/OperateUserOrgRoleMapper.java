package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import com.nodecollege.cloud.common.model.po.OperateUserOrgRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateUserOrgRoleMapper
 *
 * @author LC
 * @date 2020-09-30 16:28:46
 */
@Mapper
@Component
public interface OperateUserOrgRoleMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(OperateUserOrgRole record);

    /**
     * 根据主键id查询
     */
    OperateUserOrgRole selectByPrimaryKey(Long id);

    /**
     * 修改数据
     */
    int updateByPrimaryKey(OperateUserOrgRole record);

    /**
     * 查询管理员角色
     */
    List<OperateUserOrgRole> selectList(OperateUserOrgRole record);
}