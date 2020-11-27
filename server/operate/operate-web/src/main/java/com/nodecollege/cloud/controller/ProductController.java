package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.model.BindVO;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateProduct;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;
import com.nodecollege.cloud.common.model.vo.ProductTreeVO;
import com.nodecollege.cloud.service.ProductMenuService;
import com.nodecollege.cloud.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/17 16:25
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMenuService productMenuService;

    @ApiAnnotation(modularName = "产品信息", description = "查询产品列表")
    @PostMapping("/getProductList")
    public NCResult<ProductTreeVO> getProductList(@RequestBody QueryVO<OperateProduct> queryVO) {
        List<OperateProduct> list = productService.getProductList(queryVO.getData());
        return NCResult.ok(ProductTreeVO.getProductTree(list));
    }

    @ApiAnnotation(modularName = "产品信息", description = "添加产品")
    @PostMapping("/addProduct")
    public NCResult addProduct(@RequestBody OperateProduct product) {
        productService.addProduct(product);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品信息", description = "编辑产品")
    @PostMapping("/editProduct")
    public NCResult editProduct(@RequestBody OperateProduct product) {
        productService.editProduct(product);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品信息", description = "删除产品")
    @PostMapping("/delProduct")
    public NCResult delProduct(@RequestBody OperateProduct product) {
        productService.delProduct(product);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品菜单信息", description = "查询产品菜单树")
    @PostMapping("/getProductMenuTree")
    public NCResult<MenuVO> getProductMenuTree(@RequestBody OperateProductMenu productMenu) {
        List<MenuVO> list = productMenuService.getProductMenuList(productMenu);
        return NCResult.ok(MenuVO.getMenuTree(list));
    }

    @ApiAnnotation(modularName = "产品菜单信息", description = "清除产品菜单树缓存")
    @PostMapping("/clearProductMenuTreeCache")
    public NCResult clearProductMenuTreeCache() {
        productMenuService.clearProductMenuTreeCache();
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品菜单信息", description = "添加产品菜单")
    @PostMapping("/addProductMenu")
    public NCResult addProductMenu(@RequestBody OperateProductMenu productMenu) {
        productMenuService.addProductMenu(productMenu);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品菜单信息", description = "修改产品菜单")
    @PostMapping("/editProductMenu")
    public NCResult editProductMenu(@RequestBody OperateProductMenu productMenu) {
        productMenuService.editProductMenu(productMenu);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品菜单信息", description = "删除产品菜单")
    @PostMapping("/delProductMenu")
    public NCResult delProductMenu(@RequestBody OperateProductMenu productMenu) {
        productMenuService.delProductMenu(productMenu);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "产品菜单信息", description = "绑定产品菜单")
    @PostMapping("/bindProductMenu")
    public NCResult bindProductMenu(@RequestBody BindVO bindVO) {
        productMenuService.bindProductMenu(bindVO);
        return NCResult.ok();
    }
}
