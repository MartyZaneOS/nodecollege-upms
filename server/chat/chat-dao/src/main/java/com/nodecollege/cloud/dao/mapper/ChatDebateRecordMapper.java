package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatDebateRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * ChatDebateRecordMapper
 *
 * @author LC
 * @date 2020-04-19 11:58:31
 */
@Mapper
@Component
public interface ChatDebateRecordMapper {
    /**
     * 根据主键删除数据
     * @param debateRecordId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long debateRecordId);

    /**
     * 插入数据库记录（建议使用）
     * @param record ChatDebateRecord
     * @return int
     */
    int insertSelective(ChatDebateRecord record);

    /**
     * 根据主键id查询
     * @param debateRecordId Integer
     * @return ChatDebateRecord
     */
    ChatDebateRecord selectByPrimaryKey(Long debateRecordId);

    /**
     * 修改数据(推荐使用)
     * @param record ChatDebateRecord
     * @return int
     */
    int updateByPrimaryKeySelective(ChatDebateRecord record);

    /**
     * 查询辩论记录列表
     */
    List<ChatDebateRecord> selectListByMap(Map<String, Object> toMap);
}