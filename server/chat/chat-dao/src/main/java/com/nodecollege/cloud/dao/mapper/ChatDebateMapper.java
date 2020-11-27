package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatDebate;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * ChatDebateMapper
 *
 * @author LC
 * @date 2020-02-22 18:16:08
 */
@Mapper
@Component
public interface ChatDebateMapper {
    /**
     * 根据主键删除数据
     *
     * @param debateId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long debateId);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record ChatDebate
     * @return int
     */
    int insertSelective(ChatDebate record);

    /**
     * 根据主键id查询
     *
     * @param debateId Integer
     * @return ChatDebate
     */
    ChatDebate selectByPrimaryKey(Long debateId);

    /**
     * 修改数据(推荐使用)
     *
     * @param record ChatDebate
     * @return int
     */
    int updateByPrimaryKeySelective(ChatDebate record);

    /**
     * 查询辩论堂列表
     */
    List<ChatDebate> selectListByMap(Map<String, Object> toMap);

    /**
     * 查找辩论时间已到的数据
     */
    List<ChatDebate> selectEndDebateList(Date date);
}