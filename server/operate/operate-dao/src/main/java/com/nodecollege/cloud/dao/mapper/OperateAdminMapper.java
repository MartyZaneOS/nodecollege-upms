package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateAdmin;
import com.nodecollege.cloud.common.model.po.OperateAdminOrgRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * UpmsAdminMapper
 *
 * @author LC
 * @date 2019-11-27 19:32:32
 */
@Mapper
@Component
public interface OperateAdminMapper {
    /**
     * 根据主键删除数据
     *
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入数据库记录（不建议使用）
     *
     * @param record UpmsAdmin
     * @return int
     */
    int insert(OperateAdmin record);

    /**
     * 插入数据库记录（建议使用）
     *
     * @param record UpmsAdmin
     * @return int
     */
    int insertSelective(OperateAdmin record);

    /**
     * 根据主键id查询
     *
     * @param id Integer
     * @return UpmsAdmin
     */
    OperateAdmin selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     *
     * @param record UpmsAdmin
     * @return int
     */
    int updateByPrimaryKeySelective(OperateAdmin record);

    /**
     * 修改数据
     *
     * @param record UpmsAdmin
     * @return int
     */
    int updateByPrimaryKey(OperateAdmin record);

    /**
     * 查询数据
     *
     * @param queryMap
     * @return
     */
    List<OperateAdmin> selectAdminListByMap(Map<String, Object> queryMap);

    List<OperateAdmin> selectListByOpRoleAdmin(Map<String, Object> toMap);

    List<OperateAdmin> selectListByOrg(Map<String, Object> toMap);

    List<OperateAdmin> selectListByRoleOrg(OperateAdminOrgRole record);
}