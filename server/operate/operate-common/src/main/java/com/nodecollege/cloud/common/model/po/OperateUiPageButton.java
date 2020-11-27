package com.nodecollege.cloud.common.model.po;

import com.nodecollege.cloud.common.model.MenuVO;
import lombok.Data;

/**
 * Table: upms_ui_page_button
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-11 17:08:14
 */
@Data
public class OperateUiPageButton {
    /**
     * 主键
     */
    private Long id;

    /**
     * 页面代码
     */
    private String pageCode;

    /**
     * 按钮名称
     */
    private String buttonName;

    /**
     * 按钮代码
     */
    private String buttonCode;

    /**
     * 按钮类型 2-查询类按钮，3-操作类按钮
     */
    private Integer buttonType;

    /**
     * 页面图标
     */
    private String buttonIcon;

    /**
     * 上级按钮代码
     */
    private String parentCode;

    /**
     * 接口微服务名称
     */
    private String appName;

    /**
     * 接口地址
     */
    private String apiUrl;

    /**
     * 序号
     */
    private Integer num;

    public MenuVO getMenuVO(String productCode, String parentCode){
        MenuVO menuVO = new MenuVO();
        menuVO.setProductCode(productCode);
        menuVO.setMenuName(this.buttonName);
        menuVO.setMenuCode(this.buttonCode);
        menuVO.setMenuType(this.buttonType);
        menuVO.setMenuIcon(this.buttonIcon);
        menuVO.setParentCode("0".equals(this.parentCode) ? parentCode : this.parentCode);
        menuVO.setNum(this.num);
        menuVO.setPageCode(this.pageCode);
        menuVO.setAppName(this.appName);
        menuVO.setApiUrl(this.apiUrl);
        return menuVO;
    }
}