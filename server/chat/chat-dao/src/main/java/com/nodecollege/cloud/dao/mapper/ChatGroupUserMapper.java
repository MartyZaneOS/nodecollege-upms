package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * ChatGroupUserMapper
 *
 * @author LC
 * @date 2020-02-16 00:43:36
 */
@Mapper
@Component
public interface ChatGroupUserMapper {
    /**
     * 根据主键删除数据
     * @param groupUserId Integer
     * @return int
     */
    int deleteByPrimaryKey(Long groupUserId);

    /**
     * 插入数据库记录（建议使用）
     * @param record ChatGroupUser
     * @return int
     */
    int insertSelective(ChatGroupUser record);

    /**
     * 根据主键id查询
     * @param groupUserId Integer
     * @return ChatGroupUser
     */
    ChatGroupUser selectByPrimaryKey(Long groupUserId);

    /**
     * 修改数据(推荐使用)
     * @param record ChatGroupUser
     * @return int
     */
    int updateByPrimaryKeySelective(ChatGroupUser record);

    /**
     * 根据群组id查询群组用户列表
     */
    List<ChatGroupUser> selectGroupUserList(Map<String, Object> queryMap);
}