package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateUiPage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsUiPageMapper
 *
 * @author LC
 * @date 2020-08-11 20:28:15
 */
@Mapper
@Component
public interface OperateUiPageMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long uiPageId);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateUiPage record);

    /**
     * 根据主键id查询
     */
    OperateUiPage selectByPrimaryKey(Long uiPageId);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateUiPage record);

    /**
     * 查询前端页面
     */
    List<OperateUiPage> selectUiPageList(OperateUiPage record);
}