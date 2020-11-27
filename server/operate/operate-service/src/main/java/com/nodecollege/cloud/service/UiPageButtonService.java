package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.po.OperateUiPageButton;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/11 21:14
 */
public interface UiPageButtonService {

    /**
     * 查询按钮列表
     */
    List<OperateUiPageButton> getButtonList(OperateUiPageButton query);

    /**
     * 添加按钮
     */
    void addUiPageButton(OperateUiPageButton item);

    /**
     * 删除按钮
     */
    void delUiPageButton(OperateUiPageButton item);

    /**
     * 编辑按钮
     */
    void editUiPageButton(OperateUiPageButton item);
}
