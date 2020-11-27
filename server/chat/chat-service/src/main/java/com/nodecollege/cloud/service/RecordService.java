package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatRecord;
import com.nodecollege.cloud.common.model.vo.ChatActiveData;
import com.nodecollege.cloud.common.model.vo.ChatData;

import java.util.List;

/**
 * 聊天记录
 * @author LC
 * @date 2020/2/29 20:25
 */
public interface RecordService {

    /**
     * 根据用户id查询活跃聊天列表
     * @param queryVO
     * @return
     */
    List<ChatActiveData> getActiveChat(QueryVO<Long> queryVO);

    /**
     * 查询聊天历史记录
     * @param queryVO
     * @return
     */
    NCResult<ChatRecord> getRecord(QueryVO<ChatRecord> queryVO);
    /**
     * 添加消息
     * @return
     */
    ChatRecord addRecord(ChatRecord record);
    /**
     * 读取消息
     * @param record
     * @return
     */
    ChatRecord readRecord(ChatRecord record);
    /**
     * 撤回消息
     * @param record
     * @return
     */
    ChatRecord withdrawRecord(ChatRecord record);
}
