package com.nodecollege.cloud.common.model;

import com.alibaba.fastjson.JSONObject;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import lombok.Data;

import java.util.*;

/**
 * 数据权限
 *
 * @author LC
 * @date 2020/1/7 18:16
 */
@Data
public class DataPower {
    /**
     * 用户id
     */
    private Long memberId;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 组织机构id
     */
    private Long orgId;
    /**
     * 菜单数据权限
     * 数据格式 应用 + 接口地址
     * {
     * applicationName + url: dataPower
     * }
     */
    private Map<String, Integer> menuDataPower;

    /**
     * 数据权限1机构信息 可以操作所属机构及下级机构所有数据
     * 数据权限2机构信息 可以操作所属机构及当前机构所有下级机构数据
     * 数据权限3机构信息 可以操作所属机构的数据
     * 数据权限4机构信息 可以操作当前机构及下级所有机构数据
     * 数据权限5机构信息 仅能操作当前机构数据
     * 数据权限6机构信息 仅能操作成员自己的数据
     */
    private Set<Long> dataPower1Orgs;
    private Set<Long> dataPower2Orgs;
    private Set<Long> dataPower3Orgs;
    private Set<Long> dataPower4Orgs;

    /**
     * 接口角色信息
     * 菜单地址: 角色id列表
     */
    private Map<String, Set<Long>> apiRoleMap;

    /**
     * 角色组织机构
     * 角色id: 组织机构列表
     */
    private Map<Long, Set<Long>> roleOrgMap;

    public static void main(String[] args) {
        Map<Long, Set<Long>> test = new HashMap<>();
        test.put(1L, null);
        test.put(2L, new HashSet<>());
        test.put(3L, new HashSet<>());
        test.get(3L).add(33L);
        System.out.println(JSONObject.toJSONString(test));
        System.out.println(JSONObject.toJSONString(test.get(1L)));
        System.out.println(JSONObject.toJSONString(test.get(4L)));
        System.out.println(JSONObject.toJSONString(test.containsKey(1L)));
        System.out.println(JSONObject.toJSONString(test.containsKey(4L)));
    }

    /**
     * 获取接口数据权限
     *
     * @param appUrl 接口地址: applicationName + url
     * @return
     */
    public ApiDataPower getApiDataPower(String appUrl, Integer showAllRole) {
        if (!this.menuDataPower.containsKey(appUrl)) {
            throw new NCException("-1", "没有操作该接口的权限！");
        }
        ApiDataPower apiDataPower = new ApiDataPower();
        apiDataPower.setMemberId(this.memberId);
        apiDataPower.setTenantId(this.tenantId);
        Set<Long> orgSet = new HashSet<>();
        if (showAllRole == 0) {
            Integer dataPower = menuDataPower.get(appUrl);
            if (dataPower.equals(NCConstants.INT_0)) {
                orgSet = null;
            } else if (dataPower.equals(NCConstants.INT_1)) {
                orgSet = this.dataPower1Orgs;
            } else if (dataPower.equals(NCConstants.INT_2)) {
                orgSet = this.dataPower2Orgs;
            } else if (dataPower.equals(NCConstants.INT_3)) {
                orgSet = this.dataPower3Orgs;
            } else if (dataPower.equals(NCConstants.INT_4)) {
                orgSet = this.dataPower4Orgs;
            } else if (dataPower.equals(NCConstants.INT_5)) {
                orgSet = new HashSet<>();
                orgSet.add(this.orgId);
            } else {
                orgSet = new HashSet<>();
            }
        } else {
            Set<Long> roleSet = apiRoleMap.get(appUrl);
            orgSet = new HashSet<>();
            for (Long roleId : roleSet) {
                Set<Long> roleOrgSet = roleOrgMap.get(roleId);
                if (roleOrgSet == null) {
                    apiDataPower.setOrgList(null);
                    return apiDataPower;
                } else {
                    orgSet.addAll(roleOrgSet);
                }
            }
        }
        apiDataPower.setOrgList(orgSet == null ? null : new ArrayList<>(orgSet));
        return apiDataPower;
    }

    /**
     * 获取指定的组织机构接口数据权限
     *
     * @param appUrl
     * @param orgList
     * @return
     */
    public ApiDataPower getApiDataPower(String appUrl, Integer showAllRole, List<Long> orgList) {
        ApiDataPower apiDataPower = getApiDataPower(appUrl, showAllRole);
        if (orgList == null || orgList.isEmpty()) {
            return apiDataPower;
        }
        Set<Long> list = new HashSet<>();
        orgList.forEach(orgId -> {
            if (apiDataPower.getOrgList() == null) {
                if (this.dataPower1Orgs.contains(orgId)) {
                    list.add(orgId);
                }
            } else {
                if (apiDataPower.getOrgList().contains(orgId)) {
                    list.add(orgId);
                }
            }
        });
        apiDataPower.setOrgList(new ArrayList<>(list));
        return apiDataPower;
    }
}
