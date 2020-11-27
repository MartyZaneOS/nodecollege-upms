package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.SyncJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * SyncJobLogMapper
 *
 * @author LC
 * @date 2020-06-12 17:13:24
 */
@Mapper
@Component
public interface SyncJobLogMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long jobLogId);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(SyncJobLog record);

    /**
     * 根据主键id查询
     */
    SyncJobLog selectByPrimaryKey(Long jobLogId);

    /**
     * 查询列表
     */
    List<SyncJobLog> selectListByMap(Map<String, Object> toMap);
}