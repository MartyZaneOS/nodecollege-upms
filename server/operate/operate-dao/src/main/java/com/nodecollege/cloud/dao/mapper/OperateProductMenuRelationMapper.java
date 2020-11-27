package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateProductMenuRelation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsProductMenuRelationMapper
 *
 * @author LC
 * @date 2020-08-17 18:09:05
 */
@Mapper
@Component
public interface OperateProductMenuRelationMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateProductMenuRelation record);

    /**
     * 根据主键id查询
     */
    OperateProductMenuRelation selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateProductMenuRelation record);

    List<OperateProductMenuRelation> selectProductMenuRelationList(OperateProductMenuRelation query);
}