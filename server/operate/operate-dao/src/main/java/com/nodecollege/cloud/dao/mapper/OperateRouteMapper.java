package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateRoute;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * OperateRouteMapper
 *
 * @author LC
 * @date 2020-09-21 20:20:50
 */
@Mapper
@Component
public interface OperateRouteMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateRoute record);

    /**
     * 根据主键id查询
     */
    OperateRoute selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     * @param record OperateRoute
     * @return int
     */
    int updateByPrimaryKeySelective(OperateRoute record);

    List<OperateRoute> selectList(OperateRoute record);
}