package com.nodecollege.cloud.common.model.po;

import lombok.Data;

import java.util.Date;

/**
 * Table: upms_user_friend
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-02-22 20:11:43
 */
@Data
public class ChatFriend {
    /**
     * 用户朋友id
     */
    private Long userFriendId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 朋友id
     */
    private Long friendId;

    /**
     * 好友账号
     */
    private String friendAccount;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 来源
     * 1-搜索加好友
     * 2-群组加好友
     */
    private Integer source;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态
     * -1-黑名单
     * 0-等待我同意 发起方是朋友
     * 1-等待朋友同意 发起方是我
     * 2-已同意
     * 3-已拒绝
     */
    private Integer state;
    /**
     * 头像
     * 动态从user表中取 不存库
     */
    private String avatar;
    /**
     * 用户头像缩略图
     */
    private String avatarThumb;
}