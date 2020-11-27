package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 当前登陆用户信息
 *
 * @author LC
 * @date 2019/12/3 15:07
 */
@Data
public class MemberLoginInfo extends UserLoginInfo {
    // 成员访问令牌
    private String memberAccessToken;
    // 成员登陆信息随机字符串 发生变更 说明成员信息发生了改变，需要重新获取登陆成员信息
    private String memberUuid;
    // 成员登陆过期时间
    private Date memberExpireTime;

    // 成员id
    private Long memberId;
    // 成员账号
    private String memberAccount;
    // 成员名称
    private String memberName;
    // 成员状态 2-冻结，其他正常
    private Integer memberState;

    // 默认租户id
    private Long tenantId;
    // 默认租户名称
    private String tenantName;
    // 租户状态 2-冻结，其他正常
    private Integer tenantState;

    // 默认组织机构id
    private Long orgId;
    // 组织机构状态 2-冻结，其他正常
    private Integer orgState;
    // 是否显示全组织机构权限 0-否，1-是
    private Integer showAllOrg;

    // 默认角色id
    private Long roleId;
    // 角色状态 2-冻结，其他正常
    private Integer roleState;
    // 是否显示全角色权限 0-否，1-是
    private Integer showAllRole;

    // 拥有组织机构idList
    private List<IdName> orgList;
    // 拥有角色idList
    private List<IdName> roleList;
    // 角色对应组织机构map
    private Map<Long, List<IdName>> roleOrgMap;

    // 数据权限
    private DataPower dataPower;
    // 接口数据权限
    private ApiDataPower apiDataPower;
}
