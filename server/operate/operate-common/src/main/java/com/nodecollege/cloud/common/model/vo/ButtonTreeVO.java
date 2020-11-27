package com.nodecollege.cloud.common.model.vo;

import com.nodecollege.cloud.common.model.po.OperateUiPageButton;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 按钮树
 *
 * @author LC
 * @date 20:57 2020/8/17
 **/
@Data
public class ButtonTreeVO extends OperateUiPageButton implements Comparable<ButtonTreeVO> {
    /**
     * 子集
     */
    private List<ButtonTreeVO> children;

    public ButtonTreeVO() {
    }

    public ButtonTreeVO(OperateUiPageButton button) {
        super.setId(button.getId());
        super.setPageCode(button.getPageCode());
        super.setButtonName(button.getButtonName());
        super.setButtonCode(button.getButtonCode());
        super.setButtonType(button.getButtonType());
        super.setButtonIcon(button.getButtonIcon());
        super.setParentCode(button.getParentCode());
        super.setAppName(button.getAppName());
        super.setApiUrl(button.getApiUrl());
        super.setNum(button.getNum());
    }

    public static List<ButtonTreeVO> getButtonTree(List<OperateUiPageButton> buttonList) {
        return getButtonTree(buttonList, "0");
    }

    /**
     * 获取menuTree
     *
     * @return
     */
    public static List<ButtonTreeVO> getButtonTree(List<OperateUiPageButton> buttonList, String parentCode) {
        List<ButtonTreeVO> buttonTreeList = new ArrayList<>();
        Iterator<OperateUiPageButton> buttonIterator = buttonList.iterator();
        while (buttonIterator.hasNext()) {
            OperateUiPageButton button = buttonIterator.next();
            if (button.getParentCode().equals(parentCode)) {
                buttonTreeList.add(new ButtonTreeVO(button));
                buttonIterator.remove();
            }
        }
        Collections.sort(buttonTreeList);
        for (ButtonTreeVO buttonTreeVO : buttonTreeList) {
            buttonTreeVO.setChildren(getButtonTree(buttonList, buttonTreeVO.getButtonCode()));
        }
        if (buttonTreeList.isEmpty()) {
            return null;
        }
        return buttonTreeList;
    }

    @Override
    public int compareTo(ButtonTreeVO o) {
        return this.getNum().compareTo(o.getNum());
    }
}
