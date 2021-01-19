package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUi;
import com.nodecollege.cloud.common.model.po.OperateUiPage;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateUiMapper;
import com.nodecollege.cloud.dao.mapper.OperateUiPageMapper;
import com.nodecollege.cloud.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/11 20:01
 */
@Service
public class UiServiceImpl implements UiService {

    @Autowired
    private OperateUiMapper uiMapper;

    @Autowired
    private OperateUiPageMapper uiPageMapper;

    @Override
    public List<OperateUi> getUiList(QueryVO<OperateUi> queryVO) {
        return uiMapper.selectListByMap(queryVO.toMap());
    }

    @Override
    public void addUi(OperateUi ui) {
        NCUtils.nullOrEmptyThrow(ui.getUiCode());
        NCUtils.nullOrEmptyThrow(ui.getUiName());
        OperateUi query = new OperateUi();
        query.setUiCode(ui.getUiCode());
        List<OperateUi> uiList = uiMapper.selectUiList(query);
        if (!uiList.isEmpty()) {
            throw new NCException("-1", "该前端工程代码已存在！");
        }
        uiMapper.insertSelective(ui);
    }

    @Override
    public void delUi(OperateUi ui) {
        OperateUi ex = uiMapper.selectByPrimaryKey(ui.getUiId());
        if (ex == null) {
            return;
        }
        OperateUiPage queryUiPage = new OperateUiPage();
        queryUiPage.setUiCode(ex.getUiCode());
        List<OperateUiPage> exUiPage = uiPageMapper.selectUiPageList(queryUiPage);
        if (!exUiPage.isEmpty()) {
            throw new NCException("", "存在页面，不能删除！");
        }
        uiMapper.deleteByPrimaryKey(ex.getUiId());
    }

    @Override
    public void editUi(OperateUi ui) {
        NCUtils.nullOrEmptyThrow(ui.getUiId());
        ui.setUiCode(null);
        uiMapper.updateByPrimaryKeySelective(ui);
    }
}
