package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAppApi;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsInterfaceMapper
 *
 * @author LC
 * @date 2019-12-20 18:22:28
 */
@Mapper
@Component
public interface OperateAppApiMapper {
    /**
     * 根据主键删除数据
     * @param interfaceId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long interfaceId);

    /**
     * 插入数据库记录（不建议使用）
     * @param record UpmsInterface
     * @return int
     */
    int insert(OperateAppApi record);

    /**
     * 根据主键id查询
     * @param interfaceId Integer
     * @return UpmsInterface
     */
    OperateAppApi selectByPrimaryKey(Long interfaceId);

    /**
     * 修改数据
     * @param record UpmsInterface
     * @return int
     */
    int updateByPrimaryKey(OperateAppApi record);

    int updateByPrimaryKeySelective(OperateAppApi record);

    List<OperateAppApi> selectListByMap(Map<String, Object> queryMap);
}