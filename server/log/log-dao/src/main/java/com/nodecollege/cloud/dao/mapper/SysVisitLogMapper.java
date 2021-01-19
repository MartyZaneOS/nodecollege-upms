package com.nodecollege.cloud.dao.mapper;

import com.nodecollege.cloud.common.model.po.SysVisitLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 版权：节点学院
 * <p>
 * SysVisitLogMapper
 *
 * @author LC
 * @date 2020-11-30 11:25:31
 */
@Mapper
@Component
public interface SysVisitLogMapper {
    /**
     * 根据主键删除数据
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据库记录（建议使用）
     */
    int insertSelective(SysVisitLog record);

    /**
     * 根据主键id查询
     */
    SysVisitLog selectByPrimaryKey(Long id);

    /**
     * 修改数据(推荐使用)
     */
    int updateByPrimaryKeySelective(SysVisitLog record);

    // 统计微服务接口分钟级别访问信息
    List<SysVisitLog> countAppUrlMinuteVisit(Map<String, Object> queryMap);

    // 统计微服务接口小时级别访问信息
    List<SysVisitLog> countAppUrlHourVisit(Map<String, Object> queryMap);

    // 统计微服务接口天级别访问信息
    List<SysVisitLog> countAppUrlDayVisit(Map<String, Object> queryMap);

    // 统计微服务分钟级别访问信息
    List<SysVisitLog> countAppMinuteVisit(Map<String, Object> queryMap);

    // 统计微服务小时级别访问信息
    List<SysVisitLog> countAppHourVisit(Map<String, Object> queryMap);

    // 统计微服务天级别访问信息
    List<SysVisitLog> countAppDayVisit(Map<String, Object> queryMap);

    // 统计IP接口分钟级别访问信息
    List<SysVisitLog> countIpUrlMinuteVisit(Map<String, Object> queryMap);

    // 统计IP接口小时级别访问信息
    List<SysVisitLog> countIpUrlHourVisit(Map<String, Object> queryMap);

    // 统计IP接口天级别访问信息
    List<SysVisitLog> countIpUrlDayVisit(Map<String, Object> queryMap);

    // 统计IP分钟级别访问信息
    List<SysVisitLog> countIpMinuteVisit(Map<String, Object> queryMap);

    // 统计IP小时级别访问信息
    List<SysVisitLog> countIpHourVisit(Map<String, Object> queryMap);

    // 统计IP天级别访问信息
    List<SysVisitLog> countIpDayVisit(Map<String, Object> queryMap);

    // 统计文章小时级别访问信息
    List<SysVisitLog> countArticleHourVisit(Map<String, Object> queryMap);

    // 批量删除接口
    int deleteByParam(SysVisitLog param);

    // 查询历史访问信息
    List<SysVisitLog> selectListByMap(Map<String, Object> queryMap);
}