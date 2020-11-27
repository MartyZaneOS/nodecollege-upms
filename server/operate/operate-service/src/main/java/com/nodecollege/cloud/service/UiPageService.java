package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.po.OperateUiPage;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/11 20:39
 */
public interface UiPageService {
    /**
     * 查询前端页面列表
     */
    List<OperateUiPage> getUiPageList(OperateUiPage query);

    /**
     * 添加前端页面
     */
    void addUiPage(OperateUiPage page);

    /**
     * 删除前端页面
     */
    void delUiPage(OperateUiPage page);

    /**
     * 编辑前端页面
     */
    void editUiPage(OperateUiPage page);

    List<MenuVO> getUiPageTree();
}
