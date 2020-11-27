package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateRoleMenuMapper
 *
 * @author LC
 * @date 2020-08-31 16:23:12
 */
@Mapper
@Component
public interface OperateRoleMenuMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(OperateRoleMenu record);

    /**
     * 根据主键id查询
     */
    OperateRoleMenu selectByPrimaryKey(Long id);

    /**
     * 修改数据
     */
    int updateByPrimaryKey(OperateRoleMenu record);

    /**
     * 查询列表
     */
    List<OperateRoleMenu> selectListByMap(Map<String, Object> query);
}