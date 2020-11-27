package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateUserPassword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsUserPasswordMapper
 *
 * @author LC-ADMIN
 * @date 2019-12-05 23:20:28
 */
@Mapper
@Component
public interface OperateUserPasswordMapper {
    /**
     * 根据主键删除数据
     *
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record UpmsUserPassword
     * @return int
     */
    int insertSelective(OperateUserPassword record);

    /**
     * 根据主键id查询
     *
     * @param id Integer
     * @return UpmsUserPassword
     */
    OperateUserPassword selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     *
     * @param record UpmsUserPassword
     * @return int
     */
    int updateByPrimaryKeySelective(OperateUserPassword record);


    /**
     * 根据主键id查询
     *
     * @param userId Long
     * @return UpmsAdminPassword
     */
    List<OperateUserPassword> selectListByUserId(Long userId);
}