package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdminPassword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsAdminPasswordMapper
 *
 * @author LC
 * @date 2019-12-01 15:08:23
 */
@Mapper
@Component
public interface OperateAdminPasswordMapper {
    /**
     * 根据主键删除数据
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（不建议使用）
     * @param record UpmsAdminPassword
     * @return int
     */
    int insert(OperateAdminPassword record);

    /**
     * 插入数据库记录（建议使用）
     * @param record UpmsAdminPassword
     * @return int
     */
    int insertSelective(OperateAdminPassword record);

    /**
     * 根据主键id查询
     * @param id Integer
     * @return UpmsAdminPassword
     */
    OperateAdminPassword selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     * @param record UpmsAdminPassword
     * @return int
     */
    int updateByPrimaryKeySelective(OperateAdminPassword record);

    /**
     * 修改数据
     * @param record UpmsAdminPassword
     * @return int
     */
    int updateByPrimaryKey(OperateAdminPassword record);

    /**
     * 根据主键id查询
     * @param adminId Long
     * @return UpmsAdminPassword
     */
    List<OperateAdminPassword> selectListByAdminId(Long adminId);
}