package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateAdminOrgMapper
 *
 * @author LC
 * @date 2020-09-10 20:33:51
 */
@Mapper
@Component
public interface OperateAdminOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateAdminOrg record);

    /**
     * 根据主键id查询
     */
    OperateAdminOrg selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateAdminOrg record);

    List<OperateAdminOrg> selectList(OperateAdminOrg query);
}