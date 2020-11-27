package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsConfigMapper
 *
 * @author LC
 * @date 2020-02-07 21:58:25
 */
@Mapper
@Component
public interface OperateConfigMapper {
    /**
     * 根据主键删除数据
     *
     * @param configId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long configId);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record UpmsConfig
     * @return int
     */
    int insertSelective(OperateConfig record);

    /**
     * 根据主键id查询
     *
     * @param configId Integer
     * @return UpmsConfig
     */
    OperateConfig selectByPrimaryKey(Long configId);

    /**
     * 修改数据(推荐使用)
     *
     * @param record UpmsConfig
     * @return int
     */
    int updateByPrimaryKeySelective(OperateConfig record);

    /**
     * 查询list
     *
     * @param toMap
     * @return
     */
    List<OperateConfig> selectListByMap(Map<String, Object> toMap);
}