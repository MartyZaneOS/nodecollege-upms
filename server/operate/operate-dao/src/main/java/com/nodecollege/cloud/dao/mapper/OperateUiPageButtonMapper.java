package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateUiPageButton;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsUiPageButtonMapper
 *
 * @author LC
 * @date 2020-08-11 17:08:14
 */
@Mapper
@Component
public interface OperateUiPageButtonMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUiPageButton record);

    /**
     * 根据主键id查询
     */
    OperateUiPageButton selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateUiPageButton record);

    List<OperateUiPageButton> selectButtonList(OperateUiPageButton record);
}