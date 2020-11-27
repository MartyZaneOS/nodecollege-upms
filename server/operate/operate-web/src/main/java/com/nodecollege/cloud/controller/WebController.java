package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;
import com.nodecollege.cloud.service.ProductMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/10/28 20:30
 */
@RestController
public class WebController {

    @Autowired
    private ProductMenuService productMenuService;

    @ApiAnnotation(modularName = "产品菜单信息", description = "查询产品菜单树")
    @RequestMapping("/getWebMenuTree")
    public NCResult<MenuVO> getProductMenuTree() {
        OperateProductMenu productMenu = new OperateProductMenu();
        productMenu.setProductCode("index");
        List<MenuVO> list = productMenuService.getProductMenuList(productMenu);
        return NCResult.ok(MenuVO.getMenuTree(list));
    }
}
