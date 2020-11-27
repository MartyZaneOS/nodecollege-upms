package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.TenantMemberOrg;
import com.nodecollege.cloud.common.model.po.TenantOrg;
import com.nodecollege.cloud.common.model.po.TenantRoleOrg;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.TenantOrgMapper;
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
    private TenantOrgMapper orgMapper;

    @Override
    public List<TenantOrg> getOrgList(QueryVO<TenantOrg> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getTenantId());
        return orgMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public TenantOrg addOrg(TenantOrg org) {
        NCUtils.nullOrEmptyThrow(org.getTenantId());
        NCUtils.nullOrEmptyThrow(org.getOrgCode());
        NCUtils.nullOrEmptyThrow(org.getOrgName());
        NCUtils.nullOrEmptyThrow(org.getNum());

        QueryVO<TenantOrg> queryVO = new QueryVO<>(new TenantOrg());
        queryVO.getData().setTenantId(org.getTenantId());
        queryVO.getData().setOrgCode(org.getOrgCode());
        List<TenantOrg> exList = orgMapper.selectListByMap(queryVO.toMap());
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
        return org;
    }

    @Override
    public void editOrg(TenantOrg org) {
        NCUtils.nullOrEmptyThrow(org.getId());
        TenantOrg ex = orgMapper.selectByPrimaryKey(org.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "机构不存在！");
        if (!org.getTenantId().equals(ex.getTenantId())) {
            throw new NCException("", "不能编辑该用途机构");
        }
        org.setTenantId(null);
        org.setOrgCode(null);
        org.setParentCode(null);
        orgMapper.updateByPrimaryKeySelective(org);
    }

    @Override
    public void delOrg(TenantOrg org) {
        NCUtils.nullOrEmptyThrow(org.getId());
        TenantOrg ex = orgMapper.selectByPrimaryKey(org.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "机构不存在！");
        if (!org.getTenantId().equals(ex.getTenantId())) {
            throw new NCException("", "不能删除该用途机构");
        }

        // todo 查询绑定用户


        orgMapper.deleteByPrimaryKey(ex.getId());
    }

    @Override
    public List<TenantOrg> getOrgListByRole(QueryVO<TenantRoleOrg> queryVO) {
        return orgMapper.selectListByRole(queryVO.toMap());
    }

    @Override
    public List<TenantOrg> getOrgListByMember(QueryVO<TenantMemberOrg> queryVO) {
        return orgMapper.selectListByMember(queryVO.toMap());
    }
}
