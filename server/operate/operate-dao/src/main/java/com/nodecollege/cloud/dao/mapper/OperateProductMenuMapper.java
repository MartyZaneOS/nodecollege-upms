package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsProductMenuMapper
 *
 * @author LC
 * @date 2020-08-17 18:06:58
 */
@Mapper
@Component
public interface OperateProductMenuMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateProductMenu record);

    /**
     * 根据主键id查询
     */
    OperateProductMenu selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateProductMenu record);

    List<OperateProductMenu> selectMenuList(OperateProductMenu query);

    List<MenuVO> selectProductMenuList(OperateProductMenu query);

    List<MenuVO> selectProductMenuListByRelation(OperateProductMenu query);
}