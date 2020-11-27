package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/17 21:15
 */
public interface ProductMenuService {

    List<MenuVO> getProductMenuList(OperateProductMenu productMenu);

    void addProductMenu(OperateProductMenu productMenu);

    void editProductMenu(OperateProductMenu productMenu);

    void delProductMenu(OperateProductMenu productMenu);

    void bindProductMenu(BindVO bindVO);

    void clearProductMenuTreeCache();
}
