package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatDebateRecordUp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * ChatDebateRecordUpMapper
 *
 * @author LC
 * @date 2020-04-19 12:09:13
 */
@Mapper
@Component
public interface ChatDebateRecordUpMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long debateRecordUpId);

    /**
     * 插入数据库记录（不建议使用）
     */
    int insert(ChatDebateRecordUp record);

    /**
     * 根据主键id查询
     */
    ChatDebateRecordUp selectByPrimaryKey(Long debateRecordUpId);

    /**
     * 查询列表
     */
    List<ChatDebateRecordUp> selectList(ChatDebateRecordUp recordUp);

    /**
     * 查询是否存在
     */
    ChatDebateRecordUp selectExist(ChatDebateRecordUp recordUp);

    /**
     * 查询统计
     */
    Integer selectCount(ChatDebateRecordUp recordUp);
}