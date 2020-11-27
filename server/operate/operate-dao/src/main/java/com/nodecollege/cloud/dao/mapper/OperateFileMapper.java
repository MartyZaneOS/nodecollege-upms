package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsFileMapper
 *
 * @author LC
 * @date 2020-03-21 12:08:09
 */
@Mapper
@Component
public interface OperateFileMapper {
    /**
     * 根据主键删除数据
     *
     * @param fileId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long fileId);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record UpmsFile
     * @return int
     */
    int insertSelective(OperateFile record);

    /**
     * 根据主键id查询
     *
     * @param fileId Integer
     * @return UpmsFile
     */
    OperateFile selectByPrimaryKey(Long fileId);

    /**
     * 修改数据(推荐使用)
     *
     * @param record UpmsFile
     * @return int
     */
    int updateByPrimaryKeySelective(OperateFile record);

    /**
     * 查询list
     *
     * @param toMap
     * @return
     */
    List<OperateFile> selectListByMap(Map<String, Object> toMap);
}