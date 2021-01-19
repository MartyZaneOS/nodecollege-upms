package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;
import com.nodecollege.cloud.common.model.po.OperateUiPage;
import com.nodecollege.cloud.common.model.po.OperateUiPageButton;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateProductMenuMapper;
import com.nodecollege.cloud.dao.mapper.OperateUiPageButtonMapper;
import com.nodecollege.cloud.dao.mapper.OperateUiPageMapper;
import com.nodecollege.cloud.service.UiPageButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/11 21:14
 */
@Service
public class UiPageButtonServiceImpl implements UiPageButtonService {

    @Autowired
    private OperateUiPageMapper uiPageMapper;

    @Autowired
    private OperateUiPageButtonMapper uiPageButtonMapper;

    @Autowired
    private OperateProductMenuMapper productMenuMapper;

    @Override
    public List<OperateUiPageButton> getButtonList(OperateUiPageButton query) {
        NCUtils.nullOrEmptyThrow(query.getPageCode());
        return uiPageButtonMapper.selectButtonList(query);
    }

    @Override
    public void addUiPageButton(OperateUiPageButton item) {
        NCUtils.nullOrEmptyThrow(item.getPageCode());
        NCUtils.nullOrEmptyThrow(item.getButtonCode());
        NCUtils.nullOrEmptyThrow(item.getButtonName());
        NCUtils.nullOrEmptyThrow(item.getAppName());
        NCUtils.nullOrEmptyThrow(item.getApiUrl());
        NCUtils.nullOrEmptyThrow(item.getButtonType());
        NCUtils.nullOrEmptyThrow(item.getNum());

        OperateUiPageButton query = new OperateUiPageButton();
        query.setButtonCode(item.getButtonCode());
        List<OperateUiPageButton> exList = uiPageButtonMapper.selectButtonList(query);
        if (!exList.isEmpty()) {
            throw new NCException("", "添加失败！该代码已存在");
        }
        OperateUiPage queryPage = new OperateUiPage();
        queryPage.setPageCode(item.getButtonCode());
        List<OperateUiPage> exPageList = uiPageMapper.selectUiPageList(queryPage);
        if (!exPageList.isEmpty()){
            throw new NCException("", "该代码在页面中已存在！");
        }
        OperateProductMenu queryMenu = new OperateProductMenu();
        queryMenu.setMenuCode(item.getButtonCode());
        queryMenu.setMenuType(0);
        List<MenuVO> exMenu = productMenuMapper.selectProductMenuList(queryMenu);
        if (!exMenu.isEmpty()) {
            throw new NCException("", "添加失败！该代码已在产品菜单中存在");
        }

        if (NCUtils.isNullOrEmpty(item.getParentCode())){
            item.setParentCode("0");
//            if (item.getButtonType() != 2){
//                throw new NCException("", "顶级按钮必须是查询类按钮！");
//            }
        } else {
            query.setPageCode(item.getPageCode());
            query.setButtonCode(item.getParentCode());
            exList = uiPageButtonMapper.selectButtonList(query);
            if (exList.isEmpty()){
                throw new NCException("", "上级代码不存在！");
            }
        }
        uiPageButtonMapper.insertSelective(item);
    }

    @Override
    public void delUiPageButton(OperateUiPageButton item) {
        uiPageButtonMapper.deleteByPrimaryKey(item.getId());
    }

    @Override
    public void editUiPageButton(OperateUiPageButton item) {
        NCUtils.nullOrEmptyThrow(item.getId());
        item.setPageCode(null);
        item.setButtonCode(null);
        item.setButtonType(null);
        item.setParentCode(null);
        uiPageButtonMapper.updateByPrimaryKeySelective(item);
    }
}
