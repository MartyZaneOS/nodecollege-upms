package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.DebateDTO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;

import java.util.List;

/**
 * 辩论堂
 *
 * @author LC
 * @date 2020/4/19 13:48
 */
public interface DebateService {
    /**
     * 添加辩论堂
     */
    ChatDebate addDebate(ChatDebate debate);

    /**
     * 查询辩论堂列表 分页
     */
    NCResult<ChatDebate> getDebateList(QueryVO<ChatDebate> queryVO);

    /**
     * 查询辩论堂缓存
     */
    ChatDebate getDebateCache(Long debateId);

    /**
     * 清除辩论堂缓存
     */
    void clearDebateCache(Long debateId);

    /**
     * 批量添加节点辩论堂关系列表
     */
    void addRelation(List<ChatDebateRelation> relationList);

    /**
     * 根据节点信息查询关联辩论堂信息
     */
    NCResult<ChatDebateRelation> getRelation(QueryVO<ChatDebateRelation> queryVO);

    /**
     * 添加辩论记录
     */
    ChatDebateRecord addRecord(ChatDebateRecord record);

    /**
     * 查询辩论记录列表
     */
    NCResult<ChatDebateRecord> getRecordList(QueryVO<ChatDebateRecord> queryVO);

    /**
     * 点赞记录
     */
    ChatDebateRecordUp addRecordUp(ChatDebateRecordUp recordUp);

    /**
     * 删除点赞记录
     */
    ChatDebateRecordUp delRecordUp(ChatDebateRecordUp recordUp);

    /**
     * 接口添加辩论堂
     */
    void addDebateApi(DebateDTO debate);

    /**
     * 获取用户信息
     */
    List<OperateUser> getUserInfo(QueryVO queryVO);

    /**
     * 获取点赞用户列表
     * @param recordUp
     * @return
     */
    List<OperateUser> getUpUserList(ChatDebateRecordUp recordUp);

    /**
     * 获取我点赞的辩论记录列表
     * @param recordUp
     * @return
     */
    List<ChatDebateRecordUp> getMyUpList(ChatDebateRecordUp recordUp);
}
