package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatGroup;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.po.OperateUser;
import com.nodecollege.cloud.common.model.vo.AddGroupVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.dao.mapper.ChatGroupMapper;
import com.nodecollege.cloud.dao.mapper.ChatGroupUserMapper;
import com.nodecollege.cloud.feign.UserClient;
import com.nodecollege.cloud.service.GroupUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author LC
 * @date 2020/3/10 16:46
 */
@Service
public class GroupUserServiceImpl implements GroupUserService {
    /**
     * 群组-用户信息， 防止频繁查库
     **/
    private static final String GROUP_ID_USER_MAP = "chat:redis:groupUserMap:";

    @Autowired
    private ChatGroupMapper groupMapper;

    @Autowired
    private ChatGroupUserMapper groupUserMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 根据群组id获取群用户信息
     *
     * @param groupId
     * @return
     */
    @Override
    public List<ChatGroupUser> getGroupUser(Long groupId) {
        List<ChatGroupUser> groupUserList = redisUtils.getList(GROUP_ID_USER_MAP + groupId, ChatGroupUser.class);
        if (groupUserList == null) {
            QueryVO<ChatGroupUser> query = new QueryVO<>(new ChatGroupUser());
            query.getData().setGroupId(groupId);
            groupUserList = groupUserMapper.selectGroupUserList(query.toMap());
            QueryVO queryUser = new QueryVO();
            queryUser.setLongList(new ArrayList<>());
            for (int i = 0; i < groupUserList.size(); i++) {
                queryUser.getLongList().add(groupUserList.get(i).getUserId());
            }
            if (!queryUser.getLongList().isEmpty()) {
                NCResult<OperateUser> userNCResult = userClient.getUserListByQuery(queryUser);
                userNCResult.checkErrorThrow();
                for (int i = 0; i < groupUserList.size(); i++) {
                    for (int j = 0; j < userNCResult.getRows().size(); j++) {
                        if (groupUserList.get(i).getUserId().equals(userNCResult.getRows().get(j).getUserId())) {
                            groupUserList.get(i).setAvatar(userNCResult.getRows().get(j).getAvatar());
                            groupUserList.get(i).setAvatarThumb(userNCResult.getRows().get(j).getAvatarThumb());
                            break;
                        }
                    }
                }
            }
            int exp = new Random().nextInt(60 * 10);
            redisUtils.set(GROUP_ID_USER_MAP + groupId, groupUserList, 60 * 60 * 2 + exp);
        }
        return groupUserList;
    }

    /**
     * 更新群组用户记录阅读时间
     *
     * @param groupUser
     */
    @Override
    public void updateRecordReadTime(ChatGroupUser groupUser) {
        List<ChatGroupUser> users = getGroupUser(groupUser.getGroupId());
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(groupUser.getUserId())) {
                ChatGroupUser update = new ChatGroupUser();
                update.setGroupUserId(users.get(i).getGroupUserId());
                update.setRecordReadTime(new Date());
                users.get(i).setRecordReadTime(new Date());
                groupUser.setRecordReadTime(new Date());
                groupUserMapper.updateByPrimaryKeySelective(update);
                int exp = new Random().nextInt(60 * 10);
                redisUtils.set(GROUP_ID_USER_MAP + groupUser.getGroupId(), users, 60 * 60 * 2 + exp);
                break;
            }
        }
    }

    /**
     * 更新群组用户信息
     *
     * @param groupUser
     * @return
     */
    @Override
    public ChatGroupUser updateGroupUser(ChatGroupUser groupUser) {
        if (NCUtils.isNullOrEmpty(groupUser.getUserId()) || NCUtils.isNullOrEmpty(groupUser.getGroupId())) {
            throw new NCException("-1", "参数缺失！");
        }
        if (NCUtils.isNullOrEmpty(groupUser.getNickname()) && NCUtils.isNullOrEmpty(groupUser.getGroupState())) {
            throw new NCException("-1", "参数缺失！");
        }
        ChatGroupUser update = new ChatGroupUser();
        List<ChatGroupUser> userList = getGroupUser(groupUser.getGroupId());
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(groupUser.getUserId())) {
                ChatGroupUser user = userList.get(i);
                update.setGroupUserId(user.getGroupUserId());
                update.setNickname(groupUser.getNickname());
                update.setGroupState(groupUser.getGroupState());
                groupUserMapper.updateByPrimaryKeySelective(update);
                if (NCUtils.isNotNullOrNotEmpty(update.getNickname())) {
                    userList.get(i).setNickname(update.getNickname());
                } else if (NCUtils.isNotNullOrNotEmpty(update.getGroupState())) {
                    userList.get(i).setGroupState(update.getGroupState());
                }
                update = userList.get(i);
                int exp = new Random().nextInt(60 * 10);
                redisUtils.set(GROUP_ID_USER_MAP + groupUser.getGroupId(), userList, 60 * 60 * 2 + exp);
            }
        }
        return update;
    }

    /**
     * 添加群组用户信息
     *
     * @param groupVO
     * @return
     */
    @Override
    public List<ChatGroupUser> addGroupUser(AddGroupVO groupVO) {
        NCUtils.nullOrEmptyThrow(groupVO, new NCException("", "参数缺失！"));
        NCUtils.nullOrEmptyThrow(groupVO.getGroupId(), new NCException("", "群id不能为空！"));
        NCUtils.nullOrEmptyThrow(groupVO.getUserId(), new NCException("", "用户id不能为空！"));
        NCUtils.nullOrEmptyThrow(groupVO.getUserList(), new NCException("", "待添加用户id不能为空！"));

        List<ChatGroupUser> groupUserList = getGroupUser(groupVO.getGroupId());
        List<Long> exList = groupUserList.stream().map(item -> item.getUserId()).collect(Collectors.toList());
        if (!exList.contains(groupVO.getUserId())) {
            throw new NCException("", "只有群成员才能添加成员！");
        }

        QueryVO queryUser = new QueryVO();
        queryUser.setLongList(groupVO.getUserList());
        NCResult<OperateUser> userNCResult = userClient.getUserListByQuery(queryUser);
        userNCResult.checkErrorThrow();

        List<OperateUser> userList = userNCResult.getRows();
        // 退出群 又加群人员列表
        List<OperateUser> doubleAddList = new ArrayList<>();
        List<ChatGroupUser> result = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            if (!exList.contains(userList.get(i).getUserId())) {
                // 新加群用户
                ChatGroupUser addUser = new ChatGroupUser();
                addUser.setGroupId(groupVO.getGroupId());
                addUser.setUserId(userList.get(i).getUserId());
                addUser.setNickname(StringUtils.isBlank(userList.get(i).getNickname()) ? userList.get(i).getAccount()
                        : userList.get(i).getNickname());
                addUser.setUserType(NCConstants.INT_3);
                addUser.setState(NCConstants.INT_1);
                addUser.setGroupState(NCConstants.INT_1);
                groupUserMapper.insertSelective(addUser);
                result.add(addUser);
            } else {
                doubleAddList.add(userList.get(i));
            }
        }
        // 退出群 又加群用户处理
        for (int i = 0; i < groupUserList.size(); i++) {
            for (int j = 0; j < doubleAddList.size(); j++) {
                if (doubleAddList.get(j).getUserId().equals(groupUserList.get(i).getUserId())
                        && groupUserList.get(i).getState() == NCConstants.INT_NEGATIVE_1) {
                    ChatGroupUser updateUser = new ChatGroupUser();
                    updateUser.setGroupUserId(groupUserList.get(i).getGroupUserId());
                    updateUser.setState(NCConstants.INT_3);
                    updateUser.setNickname(StringUtils.isBlank(doubleAddList.get(i).getNickname())
                            ? doubleAddList.get(i).getAccount()
                            : doubleAddList.get(i).getNickname());
                    groupUserMapper.updateByPrimaryKeySelective(updateUser);
                    groupUserList.get(i).setState(NCConstants.INT_3);
                    groupUserList.get(i).setAvatar(doubleAddList.get(j).getAvatar());
                    groupUserList.get(i).setAvatarThumb(doubleAddList.get(j).getAvatarThumb());
                    result.add(groupUserList.get(i));
                }
            }
        }
        redisUtils.delete(GROUP_ID_USER_MAP + groupVO.getGroupId());
        return result;
    }

    /**
     * 退出群聊
     *
     * @param groupVO
     * @return
     */
    @Override
    public List<ChatGroupUser> delGroupUser(AddGroupVO groupVO) {
        NCUtils.nullOrEmptyThrow(groupVO, new NCException("", "参数缺失！"));
        NCUtils.nullOrEmptyThrow(groupVO.getGroupId(), new NCException("", "群id不能为空！"));
        NCUtils.nullOrEmptyThrow(groupVO.getUserId(), new NCException("", "用户id不能为空！"));
        NCUtils.nullOrEmptyThrow(groupVO.getUserList(), new NCException("", "待移除用户id不能为空！"));

        List<ChatGroupUser> userList = getGroupUser(groupVO.getGroupId());
        List<ChatGroupUser> liveUserList = new ArrayList<>();
        // 当前登陆人
        ChatGroupUser loginUser = null;
        for (int i = 0; i < userList.size(); i++) {
            ChatGroupUser user = userList.get(i);
            if (NCConstants.INT_NEGATIVE_1.equals(user.getState())) {
                continue;
            }
            if (user.getUserId().equals(groupVO.getUserId())) {
                loginUser = user;
            }
            liveUserList.add(user);
        }
        if (loginUser == null) {
            throw new NCException("-1", "无权操作！");
        }

        List<ChatGroupUser> delList = new ArrayList<>();
        List<ChatGroupUser> resultList = new ArrayList<>();
        if (liveUserList.size() - groupVO.getUserList().size() <= 1) {
            // 群成员人数小于等于1  解散群
            delList.addAll(liveUserList);
            ChatGroup delGroup = new ChatGroup();
            delGroup.setGroupId(groupVO.getGroupId());
            delGroup.setState(NCConstants.INT_NEGATIVE_1);
            groupMapper.updateByPrimaryKeySelective(delGroup);
        } else if (groupVO.getUserList().size() == 1 && groupVO.getUserList().get(0).equals(groupVO.getUserId())) {
            // 个人退出群聊
            for (int i = 0; i < liveUserList.size(); i++) {
                ChatGroupUser user = liveUserList.get(i);
                if (user.getUserId().equals(groupVO.getUserId())) {
                    if (NCConstants.INT_1.equals(user.getUserType())) {
                        // 群主退群 顺位选取下一个人作为群主
                        ChatGroupUser adminUser = new ChatGroupUser();
                        adminUser.setGroupUserId(liveUserList.get(1).getGroupUserId());
                        adminUser.setUserType(NCConstants.INT_1);
                        groupUserMapper.updateByPrimaryKeySelective(adminUser);
                        liveUserList.get(1).setUserType(NCConstants.INT_1);
                        resultList.add(liveUserList.get(1));
                    }
                    delList.add(user);
                }
            }
        } else {
            // 群主或者管理员移除组员
            if (NCConstants.INT_3.equals(loginUser.getUserId())) {
                throw new NCException("-1", "无权操作！");
            }
            for (int i = 0; i < liveUserList.size(); i++) {
                ChatGroupUser user = liveUserList.get(i);
                if (groupVO.getUserList().contains(user.getUserId())) {
                    if (loginUser.getUserType() >= user.getUserType()) {
                        throw new NCException("-1", "无权操作！");
                    }
                    delList.add(user);
                }
            }
        }
        for (int i = 0; i < delList.size(); i++) {
            ChatGroupUser delUser = new ChatGroupUser();
            delUser.setGroupUserId(delList.get(i).getGroupUserId());
            delUser.setUserType(NCConstants.INT_3);
            delUser.setState(NCConstants.INT_NEGATIVE_1);
            groupUserMapper.updateByPrimaryKeySelective(delUser);
            delList.get(i).setUserType(NCConstants.INT_3);
            delList.get(i).setState(NCConstants.INT_NEGATIVE_1);
            resultList.add(delList.get(i));
        }
        redisUtils.delete(GROUP_ID_USER_MAP + groupVO.getGroupId());
        return resultList;
    }
}
