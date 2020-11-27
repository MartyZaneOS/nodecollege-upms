package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 当前登陆用户信息
 *
 * @author LC
 * @date 2019/12/3 15:07
 */
@Data
public class UserLoginInfo {
    // 用户访问令牌
    private String userAccessToken;
    // 登陆信息随机字符串 发生变更 说明用户信息发生了改变，需要重新获取登陆用户信息
    private String userUuid;
    // 登陆过期时间
    private Date expireTime;

    // 用户id
    private Long userId;
    // 用户账号
    private String account;
    // 用户昵称
    private String nickname;
    // 头像
    private String avatar;
    // 用户头像缩略图
    private String avatarThumb;

    // 拥有的租户idList
    private List<IdName> tenantList;
}
