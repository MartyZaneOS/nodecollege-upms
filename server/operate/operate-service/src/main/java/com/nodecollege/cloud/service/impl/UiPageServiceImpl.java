package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateProductMenuMapper;
import com.nodecollege.cloud.dao.mapper.OperateUiMapper;
import com.nodecollege.cloud.dao.mapper.OperateUiPageButtonMapper;
import com.nodecollege.cloud.dao.mapper.OperateUiPageMapper;
import com.nodecollege.cloud.service.UiPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/8/11 20:42
 */
@Service
public class UiPageServiceImpl implements UiPageService {

    @Autowired
    private OperateUiMapper uiMapper;

    @Autowired
    private OperateUiPageMapper uiPageMapper;

    @Autowired
    private OperateUiPageButtonMapper uiPageButtonMapper;

    @Autowired
    private OperateProductMenuMapper productMenuMapper;

    @Override
    public List<OperateUiPage> getUiPageList(OperateUiPage query) {
        return uiPageMapper.selectUiPageList(query);
    }

    @Override
    public void addUiPage(OperateUiPage page) {
        NCUtils.nullOrEmptyThrow(page.getUiCode());
        NCUtils.nullOrEmptyThrow(page.getPageCode());
        NCUtils.nullOrEmptyThrow(page.getPageName());
        NCUtils.nullOrEmptyThrow(page.getPagePath());
        NCUtils.nullOrEmptyThrow(page.getNum());
        OperateUi queryUi = new OperateUi();
        queryUi.setUiCode(page.getUiCode());
        List<OperateUi> exUiList = uiMapper.selectUiList(queryUi);
        if (exUiList.isEmpty()) {
            throw new NCException("-1", "前端工程不存在！");
        }

        OperateUiPage queryUiPage = new OperateUiPage();
        queryUiPage.setPageCode(page.getPageCode());
        List<OperateUiPage> exUiPageList = uiPageMapper.selectUiPageList(queryUiPage);
        if (!exUiPageList.isEmpty()) {
            throw new NCException("-1", "页面代码已存在！");
        }
        OperateUiPageButton query = new OperateUiPageButton();
        query.setButtonCode(page.getPageCode());
        List<OperateUiPageButton> exList = uiPageButtonMapper.selectButtonList(query);
        if (!exList.isEmpty()) {
            throw new NCException("", "添加失败！该代码已在按钮中存在");
        }
        OperateProductMenu queryMenu = new OperateProductMenu();
        queryMenu.setMenuCode(page.getPageCode());
        List<MenuVO> exMenu = productMenuMapper.selectProductMenuList(queryMenu);
        if (!exMenu.isEmpty()) {
            throw new NCException("", "添加失败！该代码已在产品菜单中存在");
        }

        uiPageMapper.insertSelective(page);
    }

    @Override
    public void delUiPage(OperateUiPage page) {
        OperateUiPage ex = uiPageMapper.selectByPrimaryKey(page.getUiPageId());
        if (ex == null) {
            return;
        }
        // 有按钮数据的页面不能删除
        OperateUiPageButton queryButton = new OperateUiPageButton();
        queryButton.setPageCode(ex.getPageCode());
        List<OperateUiPageButton> exButton = uiPageButtonMapper.selectButtonList(queryButton);
        if (!exButton.isEmpty()){
            throw new NCException("-1", "该页面存在按钮信息，不能删除！");
        }
        uiPageMapper.deleteByPrimaryKey(page.getUiPageId());
    }

    @Override
    public void editUiPage(OperateUiPage page) {
        NCUtils.nullOrEmptyThrow(page.getUiPageId());
        page.setUiCode(null);
        page.setPageCode(null);
        uiPageMapper.updateByPrimaryKeySelective(page);
    }

    @Override
    public List<MenuVO> getUiPageTree() {
        List<OperateUi> uiList = uiMapper.selectUiList(new OperateUi());
        List<OperateUiPage> pageList = uiPageMapper.selectUiPageList(new OperateUiPage());
        List<MenuVO> tree = new ArrayList<>();
        for (int i = 0; i < uiList.size(); i++){
            tree.add(uiList.get(i).getMenuVO());
        }
        for (int i = 0; i < pageList.size(); i++){
            tree.add(pageList.get(i).getMenuVO());
        }
        return MenuVO.getMenuTree(tree);
    }
}
