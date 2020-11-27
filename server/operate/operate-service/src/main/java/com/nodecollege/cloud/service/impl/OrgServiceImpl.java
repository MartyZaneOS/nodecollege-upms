package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateAdminOrg;
import com.nodecollege.cloud.common.model.po.OperateOrg;
import com.nodecollege.cloud.common.model.po.OperateRoleOrg;
import com.nodecollege.cloud.common.model.po.OperateUserOrg;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateOrgMapper;
import com.nodecollege.cloud.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2020/9/8 20:11
 */
@Service
public class OrgServiceImpl implements OrgService {

    @Autowired
    private OperateOrgMapper orgMapper;

    @Override
    public List<OperateOrg> getOrgList(QueryVO<OperateOrg> queryVO) {
        return orgMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void addOrg(OperateOrg org) {
        NCUtils.nullOrEmptyThrow(org.getOrgUsage());
        NCUtils.nullOrEmptyThrow(org.getOrgCode());
        NCUtils.nullOrEmptyThrow(org.getOrgName());
        NCUtils.nullOrEmptyThrow(org.getNum());

        QueryVO<OperateOrg> queryVO = new QueryVO<>(new OperateOrg());
        queryVO.getData().setOrgUsage(org.getOrgUsage());
        queryVO.getData().setOrgCode(org.getOrgCode());
        List<OperateOrg> exList = orgMapper.selectListByMap(queryVO.toMap());
        NCUtils.notNullOrNotEmptyThrow(exList, "", "机构代码已存在！");

        if (NCUtils.isNotNullOrNotEmpty(org.getParentCode())) {
            queryVO.getData().setOrgCode(org.getParentCode());
            exList = orgMapper.selectListByMap(queryVO.toMap());
            NCUtils.nullOrEmptyThrow(exList, "", "父级组织机构不存在！");
        } else {
            org.setParentCode("0");
        }
        org.setCreateTime(new Date());
        orgMapper.insertSelective(org);
    }

    @Override
    public void editOrg(OperateOrg org) {
        NCUtils.nullOrEmptyThrow(org.getId());
        OperateOrg ex = orgMapper.selectByPrimaryKey(org.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "机构不存在！");
        if (!org.getOrgUsage().equals(ex.getOrgUsage())) {
            throw new NCException("", "不能编辑该用途机构");
        }
        org.setOrgUsage(null);
        org.setOrgCode(null);
        org.setParentCode(null);
        orgMapper.updateByPrimaryKeySelective(org);
    }

    @Override
    public void delOrg(OperateOrg org) {
        NCUtils.nullOrEmptyThrow(org.getId());
        OperateOrg ex = orgMapper.selectByPrimaryKey(org.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "机构不存在！");
        if (!org.getOrgUsage().equals(ex.getOrgUsage())) {
            throw new NCException("", "不能删除该用途机构");
        }

        // todo 查询绑定用户
        if (org.getOrgUsage() == 0) {

        } else {

        }

        orgMapper.deleteByPrimaryKey(ex.getId());
    }

    @Override
    public List<OperateOrg> getOrgListByRole(QueryVO<OperateRoleOrg> queryVO) {
        return orgMapper.selectListByRole(queryVO.toMap());
    }

    @Override
    public List<OperateOrg> getOrgListByAdmin(QueryVO<OperateAdminOrg> queryVO) {
        return orgMapper.selectListByAdmin(queryVO.toMap());
    }

    @Override
    public List<OperateOrg> getOrgListByUser(QueryVO<OperateUserOrg> queryVO) {
        return orgMapper.selectListByUser(queryVO.toMap());
    }
}
