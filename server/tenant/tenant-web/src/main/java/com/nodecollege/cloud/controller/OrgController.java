package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.TenantMemberOrg;
import com.nodecollege.cloud.common.model.po.TenantOrg;
import com.nodecollege.cloud.common.model.po.TenantRoleOrg;
import com.nodecollege.cloud.common.model.vo.NCTreeVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/9/8 20:12
 */
@RestController
@RequestMapping("/org")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "用户组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getOrgTree")
    public NCResult<NCTreeVO<TenantOrg>> getOrgTree(@RequestBody QueryVO<TenantOrg> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());

        List<TenantOrg> list = orgService.getOrgList(queryVO);
        List<NCTreeVO<TenantOrg>> treeVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TenantOrg tmp = list.get(i);
            treeVOList.add(new NCTreeVO<>(tmp, tmp.getId(), tmp.getOrgName(), tmp.getOrgCode(), tmp.getParentCode(), tmp.getNum(), tmp.getCreateTime()));
        }
        return NCResult.ok(NCTreeVO.getTree(treeVOList));
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "添加组织机构")
    @PostMapping("/addOrg")
    public NCResult addRole(@RequestBody TenantOrg org) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        org.setTenantId(loginMember.getTenantId());

        orgService.addOrg(org);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "编辑组织机构")
    @PostMapping("/editOrg")
    public NCResult editOrg(@RequestBody TenantOrg org) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        org.setTenantId(loginMember.getTenantId());
        orgService.editOrg(org);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "删除组织机构")
    @PostMapping("/delOrg")
    public NCResult delOrg(@RequestBody TenantOrg org) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        org.setTenantId(loginMember.getTenantId());
        orgService.delOrg(org);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getOrgTreeByMember")
    public NCResult<NCTreeVO<TenantOrg>> getOrgTreeByMember(@RequestBody QueryVO<TenantMemberOrg> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.getData().setTenantId(loginMember.getTenantId());

        List<TenantOrg> list = orgService.getOrgListByMember(queryVO);
        List<NCTreeVO<TenantOrg>> treeVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TenantOrg tmp = list.get(i);
            treeVOList.add(new NCTreeVO<>(tmp, tmp.getId(), tmp.getOrgName(), tmp.getOrgCode(), tmp.getParentCode(), tmp.getNum(), tmp.getCreateTime()));
        }
        return NCResult.ok(NCTreeVO.getTree(treeVOList));
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getOrgTreeByRole")
    public NCResult<NCTreeVO<TenantOrg>> getOrgTreeByRole(@RequestBody QueryVO<TenantRoleOrg> queryVO) {
        NCLoginUserVO loginMember = loginUtils.getMemberLoginInfo();
        NCUtils.nullOrEmptyThrow(loginMember, ErrorEnum.LOGIN_TIME_OUT);

        queryVO.getData().setTenantId(loginMember.getTenantId());
        List<TenantOrg> list = orgService.getOrgListByRole(queryVO);
        List<NCTreeVO<TenantOrg>> treeVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TenantOrg tmp = list.get(i);
            treeVOList.add(new NCTreeVO<>(tmp, tmp.getId(), tmp.getOrgName(), tmp.getOrgCode(), tmp.getParentCode(), tmp.getNum(), tmp.getCreateTime()));
        }
        return NCResult.ok(NCTreeVO.getTree(treeVOList));
    }

}
