package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateOrgRoleMapper
 *
 * @author LC
 * @date 2020-09-08 13:49:25
 */
@Mapper
@Component
public interface OperateRoleOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateRoleOrg record);

    /**
     * 根据主键id查询
     */
    OperateRoleOrg selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateRoleOrg record);

    List<OperateRoleOrg> selectList(OperateRoleOrg record);
}