package com.nodecollege.cloud.common.model.po;

import com.nodecollege.cloud.common.model.MenuVO;
import lombok.Data;

/**
 * Table: upms_product_menu
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-17 21:06:58
 */
@Data
public class OperateProductMenu {
    /**
     * 主键
     */
    private Long id;

    /**
     * 产品代码
     */
    private String productCode;

    // 导航平台 0-不生成导航菜单，1-pc端导航，2-移动端导航
    private Integer navPlatform;

    /**
     * 菜单代码
     */
    private String menuCode;

    /**
     * 菜单类型：0-分类导航，1-菜单页面
     */
    private Integer menuType;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 父级代码
     */
    private String parentCode;

    /**
     * 排序号
     */
    private Integer num;

    public OperateProductMenu () {}

    public MenuVO getMenuVO(){
        MenuVO menuVO = new MenuVO();
        menuVO.setProductCode(this.productCode);
        menuVO.setMenuName(this.menuName);
        menuVO.setMenuCode(this.menuCode);
        menuVO.setMenuType(this.menuType);
        menuVO.setMenuIcon(this.menuIcon);
        menuVO.setParentCode(this.parentCode);
        menuVO.setNum(this.num);
        return menuVO;
    }
}