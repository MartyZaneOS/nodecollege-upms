package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.MemberService;
import com.nodecollege.cloud.service.OrgService;
import com.nodecollege.cloud.service.RoleService;
import com.nodecollege.cloud.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author LC
 * @date 2020/10/20 20:33
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantClient tenantClient;

    @Autowired
    private OrgService orgService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MemberService memberService;

    @Override
    public void init(OperateTenant tenant) {
        // 1.添加成员
        NCResult<OperateUser> userResult = tenantClient.getUserListByUserId(tenant.getCreateUserId());
        if (!userResult.getSuccess() || userResult.getRows() == null || userResult.getRows().isEmpty()) {
            throw new NCException("", "获取用户信息失败！");
        }
        OperateUser user = userResult.getRows().get(0);

        TenantMember member = new TenantMember();
        member.setTenantId(tenant.getTenantId());
        member.setUserId(user.getUserId());
        member.setTelephone(user.getTelephone());
        member.setAccount(user.getTelephone());
        member = memberService.addMember(member);

        // 2. 添加机构
        TenantOrg org = new TenantOrg();
        org.setTenantId(tenant.getTenantId());
        org.setOrgCode(tenant.getTenantCode());
        org.setOrgName(tenant.getTenantName());
        org.setNum(1);
        org = orgService.addOrg(org);

        // 3. 绑定机构成员
        BindVO bindVO = new BindVO();
        bindVO.setTenantId(tenant.getTenantId());
        bindVO.setSourceIds(Arrays.asList(member.getMemberId()));
        bindVO.setTargetCodes(Arrays.asList(org.getOrgCode()));
        memberService.bindMemberOrg(bindVO);

        // 4. 绑定机构角色
        QueryVO<TenantRole> queryRole = new QueryVO<>(new TenantRole());
        queryRole.getData().setTenantId(tenant.getTenantId());
        List<TenantRole> roleList = roleService.getRoleList(queryRole);
        BindVO bindOrgRole = new BindVO();
        bindOrgRole.setTenantId(tenant.getTenantId());
        for (TenantRole role : roleList) {
            bindOrgRole.setSourceCodes(Arrays.asList(role.getRoleCode()));
            bindOrgRole.setTargetCodes(Arrays.asList(org.getOrgCode()));
            bindOrgRole.setMainSource(role.getRoleSource() == 1);
            roleService.bindRoleOrg(bindOrgRole);

            // 绑定成员机构角色
            if (role.getRoleType() == 1) {
                TenantMemberOrgRole mor = new TenantMemberOrgRole();
                mor.setTenantId(tenant.getTenantId());
                mor.setMemberId(member.getMemberId());
                mor.setOrgCode(org.getOrgCode());
                mor.setRoleCode(role.getRoleCode());
                mor.setRoleSource(role.getRoleSource());
                memberService.addMemberOrgRole(mor);
            }
        }
    }
}
