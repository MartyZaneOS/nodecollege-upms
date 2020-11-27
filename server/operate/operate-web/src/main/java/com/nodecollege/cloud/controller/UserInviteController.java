package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.po.OperateUserInvite;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.service.UserInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 * @date 2020/10/20 20:41
 */
@RestController
@RequestMapping("/invite")
public class UserInviteController {

    @Autowired
    private UserInviteService userInviteService;

    @ApiAnnotation(modularName = "邀请管理", description = "查询邀请列表")
    @PostMapping("/getInviteList")
    public NCResult<OperateUserInvite> getInviteList(@RequestBody QueryVO<OperateUserInvite> queryVO) {
        List<OperateUserInvite> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(queryVO.getPageSize())) {
            list = userInviteService.getInvitedList(queryVO);
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
            if (queryVO.isSort()) {
                page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
            }
            list = userInviteService.getInvitedList(queryVO);
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    @ApiAnnotation(modularName = "邀请管理", description = "邀请用户")
    @PostMapping("/inviteMember")
    public NCResult<OperateUser> inviteMember(@RequestBody OperateUserInvite inviteUser) {
        OperateUser user = userInviteService.inviteMember(inviteUser);
        return NCResult.ok(user);
    }

}
