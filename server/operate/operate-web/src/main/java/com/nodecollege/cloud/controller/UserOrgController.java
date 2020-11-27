package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateOrg;
import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrg;
import com.nodecollege.cloud.common.model.vo.NCTreeVO;
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
@RequestMapping("/user/org")
public class UserOrgController {

    @Autowired
    private OrgService orgService;

    @ApiAnnotation(modularName = "用户组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getOrgTree")
    public NCResult<NCTreeVO<OperateOrg>> getOrgTree(@RequestBody QueryVO<OperateOrg> queryVO) {
        queryVO.getData().setOrgUsage(1);
        List<OperateOrg> list = orgService.getOrgList(queryVO);
        List<NCTreeVO<OperateOrg>> treeVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            OperateOrg tmp = list.get(i);
            treeVOList.add(new NCTreeVO<>(tmp, tmp.getId(), tmp.getOrgName(), tmp.getOrgCode(), tmp.getParentCode(), tmp.getNum(), tmp.getCreateTime()));
        }
        return NCResult.ok(NCTreeVO.getTree(treeVOList));
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "添加组织机构")
    @PostMapping("/addOrg")
    public NCResult addRole(@RequestBody OperateOrg org) {
        org.setOrgUsage(1);
        orgService.addOrg(org);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "编辑组织机构")
    @PostMapping("/editOrg")
    public NCResult editOrg(@RequestBody OperateOrg org) {
        org.setOrgUsage(1);
        orgService.editOrg(org);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "删除组织机构")
    @PostMapping("/delOrg")
    public NCResult delOrg(@RequestBody OperateOrg org) {
        org.setOrgUsage(1);
        orgService.delOrg(org);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getOrgTreeByUser")
    public NCResult<NCTreeVO<OperateOrg>> getOrgTreeByUser(@RequestBody QueryVO<OperateUserOrg> queryVO) {
        List<OperateOrg> list = orgService.getOrgListByUser(queryVO);
        List<NCTreeVO<OperateOrg>> treeVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            OperateOrg tmp = list.get(i);
            treeVOList.add(new NCTreeVO<>(tmp, tmp.getId(), tmp.getOrgName(), tmp.getOrgCode(), tmp.getParentCode(), tmp.getNum(), tmp.getCreateTime()));
        }
        return NCResult.ok(NCTreeVO.getTree(treeVOList));
    }

    @ApiAnnotation(modularName = "用户组织机构信息", description = "查询组织机构列表")
    @PostMapping("/getOrgTreeByRole")
    public NCResult<NCTreeVO<OperateOrg>> getOrgTreeByRole(@RequestBody QueryVO<OperateRoleOrg> queryVO) {
        queryVO.getData().setRoleOrgUsage(1);
        List<OperateOrg> list = orgService.getOrgListByRole(queryVO);
        List<NCTreeVO<OperateOrg>> treeVOList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            OperateOrg tmp = list.get(i);
            treeVOList.add(new NCTreeVO<>(tmp, tmp.getId(), tmp.getOrgName(), tmp.getOrgCode(), tmp.getParentCode(), tmp.getNum(), tmp.getCreateTime()));
        }
        return NCResult.ok(NCTreeVO.getTree(treeVOList));
    }

}
