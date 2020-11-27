package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * SysLogMapper
 *
 * @author LC
 * @date 2020-09-27 16:37:18
 */
@Mapper
@Component
public interface SysLogMapper {
    /**
     * 根据主键删除数据
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     * @param record SysLog
     * @return int
     */
    int insertSelective(SysLog record);

    /**
     * 根据主键id查询
     * @param id Integer
     * @return SysLog
     */
    SysLog selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     * @param record SysLog
     * @return int
     */
    int updateByPrimaryKeySelective(SysLog record);

    List<SysLog> selectListByMap(Map<String, Object> queryMap);
}