package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateRoleMapper
 *
 * @author LC
 * @date 2020-08-31 16:22:36
 */
@Mapper
@Component
public interface OperateRoleMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateRole record);

    /**
     * 根据主键id查询
     */
    OperateRole selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateRole record);

    /**
     * 查询列表
     */
    List<OperateRole> selectListByMap(Map<String, Object> query);

    List<OperateRole> selectListByOrg(Map<String, Object> query);

    List<OperateRole> selectListByAdmin(Map<String, Object> query);

    List<OperateRole> selectListByUser(Map<String, Object> query);
}