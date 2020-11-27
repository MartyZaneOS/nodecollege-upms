package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.SyncJob;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * SyncJobMapper
 *
 * @author LC
 * @date 2020-06-10 15:42:30
 */
@Mapper
@Component
public interface SyncJobMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long jobId);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(SyncJob record);

    /**
     * 根据主键id查询
     */
    SyncJob selectByPrimaryKey(Long jobId);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(SyncJob record);

    /**
     * 查询list
     */
    List<SyncJob> selectList(SyncJob syncJob);

    List<SyncJob> selectListByMap(Map<String, Object> toMap);
}