package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.OperateSendMail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * OperateSendMailMapper
 *
 * @author LC
 * @date 2021-02-01 10:26:23
 */
@Mapper
@Component
public interface OperateSendMailMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(OperateSendMail record);

    /**
     * 根据主键id查询
     */
    OperateSendMail selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(OperateSendMail record);

    List<OperateSendMail> selectList(Map<String, Object> queryMap);
}