package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatFriend;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsUserFriendMapper
 *
 * @author LC
 * @date 2020-02-22 20:11:43
 */
@Mapper
@Component
public interface ChatFriendMapper {
    /**
     * 根据主键删除数据
     * @param userFriendId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long userFriendId);

    /**
     * 插入数据库记录（不建议使用）
     * @param record UpmsUserFriend
     * @return int
     */
    int insert(ChatFriend record);

    /**
     * 根据主键id查询
     * @param userFriendId Integer
     * @return UpmsUserFriend
     */
    ChatFriend selectByPrimaryKey(Long userFriendId);

    /**
     * 修改数据(推荐使用)
     * @param record UpmsUserFriend
     * @return int
     */
    int updateByPrimaryKeySelective(ChatFriend record);

    /**
     * 查询好友列表
     * @param queryMap
     * @return
     */
    List<ChatFriend> selectFriendList(Map<String, Object> queryMap);
}