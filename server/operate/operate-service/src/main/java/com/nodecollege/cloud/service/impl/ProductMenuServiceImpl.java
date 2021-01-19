package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.*;
import com.nodecollege.cloud.service.ProductMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/8/17 21:15
 */
@Service
public class ProductMenuServiceImpl implements ProductMenuService {

    @Autowired
    private OperateProductMapper productMapper;

    @Autowired
    private OperateProductMenuMapper productMenuMapper;

    @Autowired
    private OperateUiPageMapper uiPageMapper;

    @Autowired
    private OperateUiPageButtonMapper uiPageButtonMapper;

    @Autowired
    private OperateProductMenuRelationMapper productMenuRelationMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<MenuVO> getProductMenuList(OperateProductMenu productMenu) {
        NCUtils.nullOrEmptyThrow(productMenu.getProductCode(), "", "产品代码必填！");
        String key = RedisConstants.PRODUCT_MENU_LIST + productMenu.getProductCode();
        List<MenuVO> result = redisUtils.getList(key, MenuVO.class);
        if (result != null){
            return result;
        }
        result = new ArrayList<>();
        OperateProduct queryProduct = new OperateProduct();
        queryProduct.setProductCode(productMenu.getProductCode());
        List<OperateProduct> exProduct = productMapper.selectProductList(queryProduct);
        NCUtils.nullOrEmptyThrow(exProduct, "", "产品不存在！");
        if (exProduct.get(0).getProductType() == 0 || exProduct.get(0).getProductType() == 2) {
            result = productMenuMapper.selectProductMenuList(productMenu);
        } else {
            result = productMenuMapper.selectProductMenuListByRelation(productMenu);
        }
        redisUtils.set(key, result, 60 * 60 * 6);
        return result;
    }

    @Override
    public void clearProductMenuTreeCache() {
        redisUtils.delete(redisUtils.keys(RedisConstants.PRODUCT_MENU_LIST + "*"));
    }

    @Override
    public void addProductMenu(OperateProductMenu productMenu) {
        NCUtils.nullOrEmptyThrow(productMenu.getProductCode());
        NCUtils.nullOrEmptyThrow(productMenu.getNavPlatform());
        NCUtils.nullOrEmptyThrow(productMenu.getMenuCode());
        NCUtils.nullOrEmptyThrow(productMenu.getMenuType());
        NCUtils.nullOrEmptyThrow(productMenu.getNum());

        OperateProduct queryProduct = new OperateProduct();
        queryProduct.setProductCode(productMenu.getProductCode());
        List<OperateProduct> exProduct = productMapper.selectProductList(queryProduct);
        NCUtils.nullOrEmptyThrow(exProduct, "", "产品不存在！");

        if (NCUtils.isNullOrEmpty(productMenu.getParentCode())) {
            productMenu.setParentCode("0");
        } else {
            OperateProductMenu queryParent = new OperateProductMenu();
            queryParent.setProductCode(productMenu.getProductCode());
            queryParent.setMenuCode(productMenu.getParentCode());
            queryParent.setNavPlatform(productMenu.getNavPlatform());
            List<MenuVO> exParent = productMenuMapper.selectProductMenuList(queryParent);
            NCUtils.nullOrEmptyThrow(exParent, "", "不存在该父级菜单！");
            if (exParent.get(0).getMenuType() == 1) {
                throw new NCException("-1", "菜单类型的菜单不能添加下级菜单！");
            }
        }
        String menuCode = productMenu.getMenuCode();

        // 分类导航菜单中不能重复
        OperateProductMenu queryMenu = new OperateProductMenu();
        queryMenu.setMenuCode(menuCode);
        queryMenu.setMenuType(0);
        List<MenuVO> exMenu = productMenuMapper.selectProductMenuList(queryMenu);
        NCUtils.notNullOrNotEmptyThrow(exMenu, "", "代码已存在！");

        // 前端按钮中不能重复
        OperateUiPageButton queryButton = new OperateUiPageButton();
        queryButton.setButtonCode(menuCode);
        List<OperateUiPageButton> exButton = uiPageButtonMapper.selectButtonList(queryButton);
        NCUtils.notNullOrNotEmptyThrow(exButton, "", "该代码在前端页面按钮中已存在！");

        // 前端页面中不能重复
        OperateUiPage queryPage = new OperateUiPage();
        queryPage.setPageCode(menuCode);
        List<OperateUiPage> exPage = uiPageMapper.selectUiPageList(queryPage);

        if (productMenu.getMenuType() == 0) {
            NCUtils.nullOrEmptyThrow(productMenu.getMenuName());
            // 前端页面中不能重复
            NCUtils.notNullOrNotEmptyThrow(exPage, "", "该代码在前端页面中已存在！");
        } else {
            NCUtils.nullOrEmptyThrow(exPage, "", "该菜单在前端页面中不存在！");
            // 一个菜单页面  只能在一个产品中使用一次
            queryMenu.setMenuType(1);
            queryMenu.setProductCode(productMenu.getProductCode());
            queryMenu.setNavPlatform(productMenu.getNavPlatform());
            exMenu = productMenuMapper.selectProductMenuList(queryMenu);
            NCUtils.notNullOrNotEmptyThrow(exMenu, "", "该产品已经挂接该菜单页面了");

            productMenu.setMenuName(null);
            productMenu.setMenuIcon(null);
        }
        productMenuMapper.insertSelective(productMenu);
        clearProductMenuTreeCache();
    }

    @Override
    public void editProductMenu(OperateProductMenu productMenu) {
        NCUtils.nullOrEmptyThrow(productMenu.getMenuCode());
        NCUtils.nullOrEmptyThrow(productMenu.getProductCode());
        NCUtils.nullOrEmptyThrow(productMenu.getNavPlatform());
        OperateProductMenu query = new OperateProductMenu();
        query.setMenuCode(productMenu.getMenuCode());
        List<OperateProductMenu> ex = productMenuMapper.selectMenuList(query);
        NCUtils.nullOrEmptyThrow(ex, "", "菜单不存在！");
        if (ex.get(0).getMenuType() > 1){
            throw new NCException("", "按钮菜单不能修改!");
        }
        OperateProductMenu update = new OperateProductMenu();
        update.setId(ex.get(0).getId());
        update.setMenuName(productMenu.getMenuName());
        update.setMenuIcon(productMenu.getMenuIcon());
        update.setNum(productMenu.getNum());
        productMenuMapper.updateByPrimaryKeySelective(update);
        clearProductMenuTreeCache();
    }

    @Override
    public void delProductMenu(OperateProductMenu productMenu) {
        NCUtils.nullOrEmptyThrow(productMenu.getProductCode());
        NCUtils.nullOrEmptyThrow(productMenu.getMenuCode());
        NCUtils.nullOrEmptyThrow(productMenu.getNavPlatform());
        OperateProductMenu query = new OperateProductMenu();
        query.setProductCode(productMenu.getProductCode());
        query.setMenuCode(productMenu.getMenuCode());
        query.setNavPlatform(productMenu.getNavPlatform());
        List<MenuVO> ex = productMenuMapper.selectProductMenuList(query);
        NCUtils.nullOrEmptyThrow(ex, "", "菜单不存在！");

        // 查询产品菜单的下级产品信息
        OperateProduct queryProduct = new OperateProduct();
        queryProduct.setParentCode(ex.get(0).getProductCode());
        queryProduct.setProductType(1);
        List<OperateProduct> exProduct = productMapper.selectProductList(queryProduct);
        for (int i = 0; i < exProduct.size(); i++) {
            OperateProductMenuRelation queryRelation = new OperateProductMenuRelation();
            queryRelation.setProductCode(exProduct.get(i).getProductCode());
            queryRelation.setMenuCode(productMenu.getMenuCode());
            queryRelation.setNavPlatform(productMenu.getNavPlatform());
            List<OperateProductMenuRelation> exRelationList = productMenuRelationMapper.selectProductMenuRelationList(queryRelation);
            NCUtils.notNullOrNotEmptyThrow(exRelationList, "", "下级共存式产品存在绑定菜单，不能删除！");
        }
        productMenuMapper.deleteByPrimaryKey(ex.get(0).getId());
        clearProductMenuTreeCache();
    }

    @Override
    public void bindProductMenu(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceCodes());
        NCUtils.nullOrEmptyThrow(bindVO.getNavPlatform());

        OperateProduct queryProduct = new OperateProduct();
        queryProduct.setProductCode(bindVO.getSourceCodes().get(0));
        List<OperateProduct> exProduct = productMapper.selectProductList(queryProduct);
        NCUtils.nullOrEmptyThrow(exProduct);

        OperateProductMenuRelation querySource = new OperateProductMenuRelation();
        querySource.setProductCode(bindVO.getSourceCodes().get(0));
        querySource.setNavPlatform(bindVO.getNavPlatform());
        querySource.setBelongCode(exProduct.get(0).getBelongCode());
        List<OperateProductMenuRelation> exList = productMenuRelationMapper.selectProductMenuRelationList(querySource);

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getMenuCode())) {
                productMenuRelationMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getMenuCode());
            }
        });
        if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes())){
            clearProductMenuTreeCache();
            return;
        }
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                OperateProductMenuRelation add = new OperateProductMenuRelation();
                add.setProductCode(bindVO.getSourceCodes().get(0));
                add.setMenuCode(item);
                add.setBelongCode(exProduct.get(0).getBelongCode());
                add.setNavPlatform(bindVO.getNavPlatform());
                productMenuRelationMapper.insertSelective(add);
            }
        });
        clearProductMenuTreeCache();
    }
}
