package com.nodecollege.cloud.controller;

import com.nodecollege.cloud.client.utils.NCLoginUtils;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.enums.ErrorEnum;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatFriend;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.feign.UserClient;
import com.nodecollege.cloud.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LC
 * @date 2020/2/23 16:50
 */
@RestController
@RequestMapping("/myFriend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private NCLoginUtils loginUtils;

    @ApiAnnotation(modularName = "我的好友", description = "获取我的朋友列表！")
    @PostMapping("/getMyFriendList")
    public NCResult<ChatFriend> getMyFriendList(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        userFriend.setState(2);
        return NCResult.ok(friendService.getFriendList(new QueryVO<>(userFriend)));
    }

    @ApiAnnotation(modularName = "我的好友", description = "获取加我好友的请求列表！")
    @PostMapping("/getNewFriendList")
    public NCResult<ChatFriend> getNewFriendList(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        userFriend.setState(0);
        return NCResult.ok(friendService.getFriendList(new QueryVO<>(userFriend)));
    }

    @ApiAnnotation(modularName = "我的好友", description = "获取被我拒绝的请求列表！")
    @PostMapping("/getNotAgreeFriendList")
    public NCResult<ChatFriend> getNotAgreeFriendList(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        userFriend.setState(3);
        return NCResult.ok(friendService.getFriendList(new QueryVO<>(userFriend)));
    }

    @ApiAnnotation(modularName = "我的好友", description = "获取被我加入黑名单的好友列表！")
    @PostMapping("/getBlackList")
    public NCResult<ChatFriend> getBlackList(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        userFriend.setState(NCConstants.INT_NEGATIVE_1);
        return NCResult.ok(friendService.getFriendList(new QueryVO<>(userFriend)));
    }

    @ApiAnnotation(modularName = "我的好友", description = "添加好友！")
    @PostMapping("/addFriend")
    public NCResult addFriend(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        friendService.addFriend(userFriend);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "我的好友", description = "处理好友请求！")
    @PostMapping("/handleRequest")
    public NCResult handleRequest(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        friendService.handleRequest(userFriend);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "我的好友", description = "修改好友信息！")
    @PostMapping("/updateFriend")
    public NCResult updateFriend(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        friendService.updateFriend(userFriend);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "我的好友", description = "删除好友！")
    @PostMapping("/delFriend")
    public NCResult delFriend(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        friendService.delFriend(userFriend);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "我的好友", description = "添加到黑名单！")
    @PostMapping("/addBlack")
    public NCResult addBlack(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        friendService.addBlack(userFriend);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "我的好友", description = "移出黑名单！")
    @PostMapping("/delBlack")
    public NCResult delBlack(@RequestBody ChatFriend userFriend) {
        NCLoginUserVO loginUser = loginUtils.getUserLoginInfo();
        NCUtils.nullOrEmptyThrow(loginUser, ErrorEnum.LOGIN_TIME_OUT);
        userFriend.setUserId(loginUser.getLoginId());
        friendService.delBlack(userFriend);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "我的好友", description = "获取朋友列表-内部调用！")
    @PostMapping("/getFriendList")
    public NCResult<ChatFriend> getFriendList(@RequestBody QueryVO<ChatFriend> queryVO) {
        queryVO.getData().setState(2);
        return NCResult.ok(friendService.getFriendList(queryVO));
    }

}
