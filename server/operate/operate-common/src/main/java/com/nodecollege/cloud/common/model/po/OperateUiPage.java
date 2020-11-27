package com.nodecollege.cloud.common.model.po;

import com.nodecollege.cloud.common.model.MenuVO;
import lombok.Data;

/**
 * Table: upms_ui_page
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-11 16:28:15
 */
@Data
public class OperateUiPage {
    /**
     * 前端菜单页面主键
     */
    private Long uiPageId;

    /**
     * 前端代码
     */
    private String uiCode;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面代码
     */
    private String pageCode;

    /**
     * 页面路径
     */
    private String pagePath;

    /**
     * 页面图标
     */
    private String pageIcon;

    /**
     * 序号
     */
    private Integer num;

    public MenuVO getMenuVO(){
        MenuVO menuVO = new MenuVO();
        menuVO.setMenuName(this.pageName);
        menuVO.setMenuCode(this.pageCode);
        menuVO.setMenuType(1);
        menuVO.setMenuIcon(this.pageIcon);
        menuVO.setParentCode(this.uiCode);
        menuVO.setPagePath(this.pagePath);
        menuVO.setNum(this.num);
        menuVO.setPageCode(this.pageCode);
        return menuVO;
    }
}