package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatDebateRelation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * ChatDebateRelationMapper
 *
 * @author LC
 * @date 2020-04-19 13:59:28
 */
@Mapper
@Component
public interface ChatDebateRelationMapper {
    /**
     * 根据主键删除数据
     * @param debateRelationId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long debateRelationId);

    /**
     * 插入数据库记录（不建议使用）
     * @param record ChatDebateRelation
     * @return int
     */
    int insert(ChatDebateRelation record);

    /**
     * 根据主键id查询
     * @param debateRelationId Integer
     * @return ChatDebateRelation
     */
    ChatDebateRelation selectByPrimaryKey(Long debateRelationId);

    /**
     * 修改数据(推荐使用)
     * @param record ChatDebateRelation
     * @return int
     */
    int updateByPrimaryKeySelective(ChatDebateRelation record);

    /**
     * 查询辩论节点关系列表
     */
    List<ChatDebateRelation> selectListByMap(Map<String, Object> toMap);

    /**
     * 更新辩论关联记录结束标记
     */
    int updateFinish(Long debateId);
}