package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.OperateProduct;
import com.nodecollege.cloud.common.model.po.OperateProductMenu;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateProductMapper;
import com.nodecollege.cloud.dao.mapper.OperateProductMenuMapper;
import com.nodecollege.cloud.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/17 20:26
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private OperateProductMapper productMapper;

    @Autowired
    private OperateProductMenuMapper productMenuMapper;

    @Override
    public List<OperateProduct> getProductList(OperateProduct data) {
        return productMapper.selectProductList(data);
    }

    @Override
    public void addProduct(OperateProduct product) {
        NCUtils.nullOrEmptyThrow(product.getProductName());
        NCUtils.nullOrEmptyThrow(product.getProductCode());
        NCUtils.nullOrEmptyThrow(product.getProductType());

        OperateProduct query = new OperateProduct();
        query.setProductCode(product.getProductCode());
        List<OperateProduct> exLit = productMapper.selectProductList(query);
        if (!exLit.isEmpty()) {
            throw new NCException("", "该产品代码已存在！");
        }
        // 顶级产品
        if (product.getProductType() == 0) {
            NCUtils.nullOrEmptyThrow(product.getProductUsage());
            product.setParentCode("0");
            product.setTopCode(product.getProductCode());
            product.setBelongCode(product.getProductCode());
        } else {
            NCUtils.nullOrEmptyThrow(product.getParentCode());
            query.setProductCode(product.getParentCode());
            exLit = productMapper.selectProductList(query);
            if (exLit.isEmpty()) {
                throw new NCException("", "父级产品不存在！");
            }
            OperateProduct parent = exLit.get(0);

            // 父级产品类型是2-排斥式产品，则下级产品归属改为父级代码
            if (parent.getProductType() == 2) {
                product.setBelongCode(parent.getProductCode());
            } else {
                product.setBelongCode(parent.getBelongCode());
            }
            // 产品用途和顶级产品代码不变
            product.setProductUsage(parent.getProductUsage());
            product.setTopCode(parent.getTopCode());
        }
        productMapper.insertSelective(product);
    }

    @Override
    public void editProduct(OperateProduct product) {
        NCUtils.nullOrEmptyThrow(product.getId());
        product.setProductCode(null);
        product.setProductType(null);
        product.setProductUsage(null);
        product.setParentCode(null);
        product.setTopCode(null);
        product.setBelongCode(null);
        OperateProduct ex = productMapper.selectByPrimaryKey(product.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "产品不存在！");
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public void delProduct(OperateProduct product) {
        NCUtils.nullOrEmptyThrow(product.getId());
        OperateProduct ex = productMapper.selectByPrimaryKey(product.getId());
        NCUtils.nullOrEmptyThrow(ex, "", "产品不存在！");

        // 查询是否有下级产品
        OperateProduct query = new OperateProduct();
        query.setParentCode(ex.getProductCode());
        List<OperateProduct> exList = productMapper.selectProductList(query);
        NCUtils.notNullOrNotEmptyThrow(exList, "", "存在下级产品，不能删除！");

        // 查询是否绑定了菜单信息
        OperateProductMenu queryMenu = new OperateProductMenu();
        queryMenu.setProductCode(ex.getProductCode());
        List<MenuVO> exMenu = productMenuMapper.selectProductMenuList(queryMenu);
        NCUtils.notNullOrNotEmptyThrow(exMenu, "", "存在绑定菜单信息！");

        productMapper.deleteByPrimaryKey(product.getId());
    }
}
