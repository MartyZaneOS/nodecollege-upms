package com.nodecollege.cloud.common.model.vo;

import com.nodecollege.cloud.common.model.po.OperateProduct;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 产品树
 *
 * @author LC
 * @date 20:57 2020/8/17
 **/
@Data
public class ProductTreeVO extends OperateProduct {
    /**
     * 子集
     */
    private List<ProductTreeVO> children;

    public ProductTreeVO() {
    }

    public ProductTreeVO(OperateProduct product) {
        super.setId(product.getId());
        super.setProductCode(product.getProductCode());
        super.setProductName(product.getProductName());
        super.setProductCode(product.getProductCode());
        super.setProductType(product.getProductType());
        super.setProductUsage(product.getProductUsage());
        super.setProductIcon(product.getProductIcon());
        super.setParentCode(product.getParentCode());
        super.setProductDesc(product.getProductDesc());
    }

    public static List<ProductTreeVO> getProductTree(List<OperateProduct> productList) {
        return getProductTree(productList, "0");
    }

    /**
     * 获取productTree
     *
     * @return
     */
    public static List<ProductTreeVO> getProductTree(List<OperateProduct> productList, String parentCode) {
        List<ProductTreeVO> productTreeList = new ArrayList<>();
        Iterator<OperateProduct> productIterator = productList.iterator();
        while (productIterator.hasNext()) {
            OperateProduct product = productIterator.next();
            if (product.getParentCode().equals(parentCode)) {
                productTreeList.add(new ProductTreeVO(product));
                productIterator.remove();
            }
        }
        for (ProductTreeVO productTreeVO : productTreeList) {
            productTreeVO.setChildren(getProductTree(productList, productTreeVO.getProductCode()));
        }
        if (productTreeList.isEmpty()) {
            return null;
        }
        return productTreeList;
    }
}
