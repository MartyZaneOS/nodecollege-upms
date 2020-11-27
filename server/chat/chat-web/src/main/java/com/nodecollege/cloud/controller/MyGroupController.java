package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.vo.AddGroupVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LC
 * @date 2020/2/23 11:16
 */
@RestController
@RequestMapping("/chat/myGroup")
public class MyGroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "我的群组", description = "查询我的群组列表")
    @PostMapping("/getGroupList")
    public NCResult<ChatGroupUser> getGroupList(@RequestBody QueryVO<ChatGroupUser> queryVO) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        queryVO.setUserId(loginUser.getLoginId());
        List<ChatGroupUser> chatGroupList = groupService.getGroupList(queryVO);
        return NCResult.ok(chatGroupList);
    }

    @ApiAnnotation(modularName = "我的群组", description = "创建群组")
    @PostMapping("/addMyGroup")
    public NCResult addMyGroup(@RequestBody AddGroupVO addGroupVO) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        addGroupVO.setUserId(loginUser.getLoginId());
        addGroupVO.setGroupType(NCConstants.INT_4);
        groupService.addGroup(addGroupVO);
        return NCResult.ok("添加群组成功！");
    }

}
