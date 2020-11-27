package com.nodecollege.cloud.common.model.po;

import com.nodecollege.cloud.common.model.MenuVO;
import lombok.Data;

/**
 * Table: upms_ui
 * 版权：节点学院
 *
 * @author LC
 * @date 2020-08-11 19:48:53
 */
@Data
public class OperateUi {
    /**
     * 主键
     */
    private Long uiId;

    /**
     * 前端名称
     */
    private String uiName;

    /**
     * 前端代码
     */
    private String uiCode;

    /**
     * 描述
     */
    private String uiDesc;

    public MenuVO getMenuVO(){
        MenuVO menuVO = new MenuVO();
        menuVO.setMenuName(this.uiName);
        menuVO.setMenuCode(this.uiCode);
        menuVO.setMenuType(1);
        menuVO.setParentCode("0");
        menuVO.setNum(0);
        return menuVO;
    }
}