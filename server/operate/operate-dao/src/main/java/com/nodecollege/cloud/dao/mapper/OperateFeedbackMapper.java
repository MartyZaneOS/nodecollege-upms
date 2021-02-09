package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateFeedbackMapper
 *
 * @author LC
 * @date 2021-02-01 15:50:04
 */
@Mapper
@Component
public interface OperateFeedbackMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateFeedback record);

    /**
     * 根据主键id查询
     */
    OperateFeedback selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateFeedback record);

    List<OperateFeedback> selectList(Map<String, Object> queryMap);
}