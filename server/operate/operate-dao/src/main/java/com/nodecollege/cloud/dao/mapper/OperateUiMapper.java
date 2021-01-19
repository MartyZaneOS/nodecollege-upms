package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateUi;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsUiMapper
 *
 * @author LC
 * @date 2020-08-11 19:48:53
 */
@Mapper
@Component
public interface OperateUiMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long uiId);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUi record);

    /**
     * 根据主键id查询
     */
    OperateUi selectByPrimaryKey(Long uiId);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateUi record);

    /**
     * 查询前端工程列表
     */
    List<OperateUi> selectUiList(OperateUi record);

    List<OperateUi> selectListByMap(Map<String, Object> queryMap);
}