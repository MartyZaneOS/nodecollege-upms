package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.po.OperateProduct;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/17 20:26
 */
public interface ProductService {

    List<OperateProduct> getProductList(OperateProduct data);

    void addProduct(OperateProduct product);

    void editProduct(OperateProduct product);

    void delProduct(OperateProduct product);
}
