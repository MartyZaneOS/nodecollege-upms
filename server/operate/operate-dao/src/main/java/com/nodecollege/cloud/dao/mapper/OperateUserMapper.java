package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserOrgRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsUserMapper
 *
 * @author LC-ADMIN
 * @date 2019-12-03 23:25:09
 */
@Mapper
@Component
public interface OperateUserMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUser record);

    /**
     * 根据主键id查询
     */
    OperateUser selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateUser record);

    /**
     * 查询用户list
     */
    List<OperateUser> selectUserListByMap(Map<String, Object> queryMap);

    /**
     * 根据用户名称或者手机号查询用户信息
     */
    List<OperateUser> getUserListByNickname(Map<String, Object> toMap);

    List<OperateUser> selectListByOrg(Map<String, Object> toMap);

    List<OperateUser> selectListByRoleOrg(OperateUserOrgRole record);
}