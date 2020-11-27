package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatGroup;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * ChatGroupMapper
 *
 * @author LC
 * @date 2020-02-16 00:19:51
 */
@Mapper
@Component
public interface ChatGroupMapper {
    /**
     * 根据主键删除数据
     *
     * @param groupId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long groupId);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record ChatGroup
     * @return int
     */
    int insertSelective(ChatGroup record);

    /**
     * 根据主键id查询
     *
     * @param groupId Integer
     * @return ChatGroup
     */
    ChatGroup selectByPrimaryKey(Long groupId);

    /**
     * 修改数据(推荐使用)
     *
     * @param record ChatGroup
     * @return int
     */
    int updateByPrimaryKeySelective(ChatGroup record);

    /**
     * 查询群组信息
     *
     * @param queryMap
     */
    List<ChatGroup> selectGroupListByMap(Map<String, Object> queryMap);

    /**
     * 查询我的群组信息
     *
     * @param queryMap
     */
    List<ChatGroupUser> selectGroupListByGroupUser(Map<String, Object> queryMap);
}