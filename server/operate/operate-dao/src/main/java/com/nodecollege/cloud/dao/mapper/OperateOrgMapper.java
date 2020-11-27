package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateOrgMapper
 *
 * @author LC
 * @date 2020-09-08 20:08:02
 */
@Mapper
@Component
public interface OperateOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateOrg record);

    /**
     * 根据主键id查询
     */
    OperateOrg selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateOrg record);

    /**
     * 查询机构列表
     */
    List<OperateOrg> selectListByMap(Map<String, Object> queryMap);

    List<OperateOrg> selectListByRole(Map<String, Object> queryMap);

    List<OperateOrg> selectListByAdmin(Map<String, Object> queryMap);

    List<OperateOrg> selectListByUser(Map<String, Object> queryMap);
}