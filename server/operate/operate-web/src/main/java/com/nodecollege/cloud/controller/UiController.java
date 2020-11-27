package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.MenuVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUi;
import com.nodecollege.cloud.common.model.po.OperateUiPage;
import com.nodecollege.cloud.common.model.po.OperateUiPageButton;
import com.nodecollege.cloud.common.model.vo.ButtonTreeVO;
import com.nodecollege.cloud.service.UiPageButtonService;
import com.nodecollege.cloud.service.UiPageService;
import com.nodecollege.cloud.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/6/10 18:59
 */
@RestController
@RequestMapping("/ui")
public class UiController {

    @Autowired
    private UiService uiService;

    @Autowired
    private UiPageService uiPageService;

    @Autowired
    private UiPageButtonService uiPageButtonService;

    @ApiAnnotation(modularName = "前端工程", description = "查询前端列表")
    @PostMapping("/getUiList")
    public NCResult<OperateUi> getUiList(@RequestBody QueryVO queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateUi> list = uiService.getUiList();
        return NCResult.ok(list, page.getTotal());
    }

    @ApiAnnotation(modularName = "前端工程", description = "添加前端")
    @PostMapping("/addUi")
    public NCResult addUi(@RequestBody OperateUi ui) {
        uiService.addUi(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端工程", description = "删除前端")
    @PostMapping("/delUi")
    public NCResult delUi(@RequestBody OperateUi ui) {
        uiService.delUi(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端工程", description = "修改前端")
    @PostMapping("/editUi")
    public NCResult editUi(@RequestBody OperateUi ui) {
        uiService.editUi(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端页面", description = "查询前端页面信息")
    @PostMapping("/getUiPageList")
    public NCResult<OperateUiPage> getUiPageList(@RequestBody QueryVO<OperateUiPage> queryVO) {
        if (!NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            List<OperateUiPage> list = uiPageService.getUiPageList(queryVO.getData());
            return NCResult.ok(list, page.getTotal());
        }
        return NCResult.ok(uiPageService.getUiPageList(queryVO.getData()));
    }

    @ApiAnnotation(modularName = "前端页面", description = "查询前端页面树")
    @PostMapping("/getUiPageTree")
    public NCResult<MenuVO> getUiPageTree() {
        return NCResult.ok(uiPageService.getUiPageTree());
    }

    @ApiAnnotation(modularName = "前端页面", description = "添加前端页面")
    @PostMapping("/addUiPage")
    public NCResult addUiPage(@RequestBody OperateUiPage ui) {
        uiPageService.addUiPage(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端页面", description = "删除前端页面")
    @PostMapping("/delUiPage")
    public NCResult delUiPage(@RequestBody OperateUiPage ui) {
        uiPageService.delUiPage(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端页面", description = "修改前端页面")
    @PostMapping("/editUiPage")
    public NCResult editUiPage(@RequestBody OperateUiPage ui) {
        uiPageService.editUiPage(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端页面按钮", description = "查询按钮树")
    @PostMapping("/getButtonTree")
    public NCResult<ButtonTreeVO> getButtonTree(@RequestBody OperateUiPageButton query) {
        List<OperateUiPageButton> list = uiPageButtonService.getButtonList(query);
        int total = list.size();
        return NCResult.ok(ButtonTreeVO.getButtonTree(list), total);
    }

    @ApiAnnotation(modularName = "前端页面按钮", description = "添加前端页面按钮")
    @PostMapping("/addUiPageButton")
    public NCResult addUiPageButton(@RequestBody OperateUiPageButton ui) {
        uiPageButtonService.addUiPageButton(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端页面按钮", description = "删除前端页面按钮")
    @PostMapping("/delUiPageButton")
    public NCResult delUiPageButton(@RequestBody OperateUiPageButton ui) {
        uiPageButtonService.delUiPageButton(ui);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "前端页面按钮", description = "修改前端页面按钮")
    @PostMapping("/editUiPageButton")
    public NCResult editUiPageButton(@RequestBody OperateUiPageButton ui) {
        uiPageButtonService.editUiPageButton(ui);
        return NCResult.ok();
    }
}
