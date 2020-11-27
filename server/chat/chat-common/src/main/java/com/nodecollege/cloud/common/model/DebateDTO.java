package com.nodecollege.cloud.common.model;

import com.nodecollege.cloud.common.model.po.ChatDebate;
import com.nodecollege.cloud.common.model.po.ChatDebateRelation;
import lombok.Data;

import java.util.List;

/**
 * @author LC
 * @date 2020/4/22 21:53
 */
@Data
public class DebateDTO {
    /**
     * 辩论堂数据
     */
    private ChatDebate debate;
    /**
     * 关联数据
     */
    private List<ChatDebateRelation> relationList;
    /**
     * 关联作者id列表
     */
    private List<Long> authorIdList;
    /**
     * 关联文章id列表
     */
    private List<Long> articleIdList;
}
