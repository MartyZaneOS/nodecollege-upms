package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateUserInvite;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsUserInvitationMapper
 *
 * @author LC
 * @date 2019-12-12 15:56:48
 */
@Mapper
@Component
public interface OperateUserInviteMapper {
    /**
     * 根据主键删除数据
     * @return int
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUserInvite record);

    /**
     * 根据主键id查询
     */
    OperateUserInvite selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateUserInvite record);

    /**
     * 查询用户邀请列表
     */
    List<OperateUserInvite> selectListByMap(Map<String, Object> queryMap);
}