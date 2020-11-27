package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权：节点学院
 * <p>
 * UpmsProductMapper
 *
 * @author LC
 * @date 2020-08-17 20:18:43
 */
@Mapper
@Component
public interface OperateProductMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateProduct record);

    /**
     * 根据主键id查询
     */
    OperateProduct selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateProduct record);

    /**
     * 查询产品信息
     */
    List<OperateProduct> selectProductList(OperateProduct record);
}