package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatRecord;
import com.nodecollege.cloud.common.model.vo.ChatActiveData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * ChatRecordMapper
 *
 * @author LC
 * @date 2020-02-16 01:02:23
 */
@Mapper
@Component
public interface ChatRecordMapper {
    /**
     * 根据主键删除数据
     *
     * @param recordId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long recordId);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record ChatRecord
     * @return int
     */
    int insertSelective(ChatRecord record);

    /**
     * 根据主键id查询
     *
     * @param recordId Integer
     * @return ChatRecord
     */
    ChatRecord selectByPrimaryKey(Long recordId);

    /**
     * 修改数据(推荐使用)
     *
     * @param record ChatRecord
     * @return int
     */
    int updateByPrimaryKeySelective(ChatRecord record);

    /**
     * 查询活跃聊天列表
     *
     * @param userId
     */
    List<ChatActiveData> getActiveChatList(Long userId);

    /**
     * 查询聊天记录
     */
    List<ChatRecord> selectRecordList(Map<String, Object> queryMap);

    /**
     * 查询朋友最近一次接收消息
     */
    List<ChatRecord> selectFriendMaxReceiveTimeRecord(Map<String, Object> queryMap);

    /**
     * 查询新消息数
     */
    Integer selectChatNewsNum(Map<String, Object> queryMap);
}