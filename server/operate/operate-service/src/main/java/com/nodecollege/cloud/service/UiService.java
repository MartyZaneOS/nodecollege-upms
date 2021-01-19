package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUi;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/11 20:00
 */
public interface UiService {
    /**
     * 获取前端列表
     */
    List<OperateUi> getUiList(QueryVO<OperateUi> queryVO);

    /**
     * 添加前端
     */
    void addUi(OperateUi ui);

    /**
     * 删除前端
     */
    void delUi(OperateUi ui);

    /**
     * 编辑前端
     */
    void editUi(OperateUi ui);

}
