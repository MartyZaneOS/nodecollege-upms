package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateUserOrgMapper
 *
 * @author LC
 * @date 2020-09-30 20:54:11
 */
@Mapper
@Component
public interface OperateUserOrgMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUserOrg record);

    /**
     * 根据主键id查询
     */
    OperateUserOrg selectByPrimaryKey(Long id);

    List<OperateUserOrg> selectList(OperateUserOrg query);
}