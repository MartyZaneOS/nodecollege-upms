package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.ChatConstants;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.exception.NCException;
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
import com.nodecollege.cloud.service.GroupService;
import com.nodecollege.cloud.service.GroupUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author LC
 * @date 2020/2/22 19:24
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private ChatGroupMapper groupMapper;

    @Autowired
    private ChatGroupUserMapper groupUserMapper;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询群组列表
     *
     * @param queryVO
     * @return
     */
    @Override
    public List<ChatGroupUser> getGroupList(QueryVO<ChatGroupUser> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getUserId(), new NCException("-1", "用户id必填！"));
        return groupMapper.selectGroupListByGroupUser(queryVO.toMap());
    }

    /**
     * 添加群组
     *
     * @param addGroupVO
     * @return
     */
    @Override
    public ChatGroup addGroup(AddGroupVO addGroupVO) {
        NCUtils.nullOrEmptyThrow(addGroupVO.getUserId(), new NCException("-1", "创建人不能为空！"));
        NCUtils.nullOrEmptyThrow(addGroupVO.getGroupType(), new NCException("-1", "群组类型不能为空！"));
        NCUtils.nullOrEmptyThrow(addGroupVO.getGroupName(), new NCException("-1", "群组名称不能为空！"));

        QueryVO queryUser = new QueryVO();
        queryUser.setLongList(addGroupVO.getUserList());
        queryUser.getLongList().add(addGroupVO.getUserId());
        List<OperateUser> userList = userClient.getUserListByQuery(queryUser).getRows();
        if (userList == null || userList.size() < 3) {
            throw new NCException("", "群聊天不能少于三个人！");
        }

        Page page = PageHelper.startPage(1, 1);
        page.setOrderBy("group_no desc");
        List<ChatGroup> exGroupList = groupMapper.selectGroupListByMap(null);
        Long maxGroupNo = 1L;
        int cs = 0;
        while (true && !exGroupList.isEmpty()) {
            maxGroupNo = exGroupList.get(0).getGroupNo() + new Random().nextInt(5) + 1;
            if (NCUtils.isNullOrEmpty(redisUtils.get("chat:addGroup:maxGroupNo:" + maxGroupNo, Long.class))) {
                redisUtils.set("chat:addGroup:maxGroupNo:" + maxGroupNo, maxGroupNo, 100);
                break;
            }
            if (cs++ > 3) {
                page = PageHelper.startPage(1, 1);
                page.setOrderBy("group_no desc");
                exGroupList = groupMapper.selectGroupListByMap(null);
            }
        }

        addGroupVO.setGroupNo(maxGroupNo);
        addGroupVO.setNoticeSetting(NCConstants.INT_1);
        addGroupVO.setSetting(NCConstants.INT_1);
        addGroupVO.setTenantId(null);
        addGroupVO.setOrgId(null);
        addGroupVO.setCreateTime(new Date());
        addGroupVO.setUpdateTime(new Date());
        addGroupVO.setState(NCConstants.INT_1);
        groupMapper.insertSelective(addGroupVO);

        for (int i = 0; i < userList.size(); i++) {
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setGroupId(addGroupVO.getGroupId());
            chatGroupUser.setUserId(userList.get(i).getUserId());
            chatGroupUser.setNickname(StringUtils.isBlank(userList.get(i).getNickname()) ? userList.get(i).getAccount()
                    : userList.get(i).getNickname());
            chatGroupUser.setUserType(NCConstants.INT_3);
            if (userList.get(i).getUserId().equals(addGroupVO.getUserId())) {
                chatGroupUser.setUserType(NCConstants.INT_1);
            }
            chatGroupUser.setState(NCConstants.INT_1);
            chatGroupUser.setGroupState(NCConstants.INT_1);
            groupUserMapper.insertSelective(chatGroupUser);
        }
        redisUtils.set("chat:addGroup:maxGroupNo:" + maxGroupNo, maxGroupNo, 10);
        return addGroupVO;
    }

    /**
     * 添加租户群
     *
     * @param addGroupVO
     * @return
     */
    @Override
    public ChatGroup addTenantGroup(AddGroupVO addGroupVO) {
        NCUtils.nullOrEmptyThrow(addGroupVO.getUserId(), new NCException("-1", "创建人不能为空！"));
        NCUtils.nullOrEmptyThrow(addGroupVO.getGroupType(), new NCException("-1", "群组类型不能为空！"));
        NCUtils.nullOrEmptyThrow(addGroupVO.getGroupName(), new NCException("-1", "群组名称不能为空！"));
        NCUtils.nullOrEmptyThrow(addGroupVO.getTenantId(), new NCException("", "公司群租户id必填！"));

//        UpmsTenant queryTenant = new UpmsTenant();
//        queryTenant.setTenantId(addGroupVO.getTenantId());
//        NCResult<UpmsTenant> tenant = userClient.getTenantById(queryTenant);
//        tenant.checkErrorThrow();
//        NCUtils.nullOrEmptyThrow(tenant.getRows(), new NCException("-1", "租户不存在！"));

        Integer groupType = addGroupVO.getGroupType();
        if (NCConstants.INT_1.equals(groupType)) {
            addGroupVO.setOrgId(null);
            // todo 全员群 添加租户所有成员
        } else if (NCConstants.INT_2.equals(groupType)) {
            NCUtils.nullOrEmptyThrow(addGroupVO.getOrgId(), new NCException("", "部门群组织机构id必填！"));

//            UpmsOrg queryOrg = new UpmsOrg();
//            queryOrg.setTenantId(addGroupVO.getTenantId());
//            queryOrg.setOrgId(addGroupVO.getOrgId());
//            NCResult<UpmsOrg> orgNCResult = userClient.getOrgList(queryOrg);
//            orgNCResult.checkErrorThrow();
//            NCUtils.nullOrEmptyThrow(orgNCResult, new NCException("-1", "组织机构不存在！"));
            // todo 部门群 添加这个组织机构及所有下级组织机构的成员

        } else {
            addGroupVO.getUserList().forEach(user -> {
                NCUtils.nullOrEmptyThrow(user, new NCException("", "添加成员id不能为空！"));
            });
            addGroupVO.setOrgId(null);
            // todo 校验成员都必须为该公司成员
        }
        return null;
    }

    /**
     * 添加辩论群
     *
     * @param addGroupVO
     * @return
     */
    @Override
    public ChatGroup addDebateGroup(AddGroupVO addGroupVO) {
        NCUtils.nullOrEmptyThrow(addGroupVO.getDebateId(), new NCException("", "群组id必填！"));
        return null;
    }

    /**
     * 修改群组
     *
     * @param groupUser
     */
    @Override
    public ChatGroup updateGroup(ChatGroupUser groupUser) {
        NCUtils.nullOrEmptyThrow(groupUser.getGroupId(), new NCException("-1", "群组id不能为空！"));
        ChatGroup exGroup = groupMapper.selectByPrimaryKey(groupUser.getGroupId());
        checkUpdateOrDelete(groupUser, exGroup);

        ChatGroupUser user = null;
        List<ChatGroupUser> userList = groupUserService.getGroupUser(groupUser.getGroupId());
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(groupUser.getUserId())) {
                user = userList.get(i);
            }
        }
        NCUtils.nullOrEmptyThrow(user, new NCException("", "用户不存在！"));
        if (NCUtils.isNotNullOrNotEmpty(groupUser.getNotice()) && !NCConstants.INT_1.equals(exGroup.getNoticeSetting())) {
            if (NCConstants.INT_2.equals(exGroup.getNoticeSetting())) {
                if (user.getUserType().equals(NCConstants.INT_3)) {
                    throw new NCException("", "只有管理员可以修改该群公告！");
                }
            }
            if (NCConstants.INT_3.equals(exGroup.getNoticeSetting())) {
                if (!user.getUserType().equals(NCConstants.INT_1)) {
                    throw new NCException("", "只有群主可以修改该群公告！");
                }
            }
        }
        if (NCUtils.isNotNullOrNotEmpty(groupUser.getSetting())) {
            if (user.getUserType().equals(NCConstants.INT_3)) {
                throw new NCException("", "只有群主或管理员可以修改发言限制！");
            }
        }

        groupUser.setUpdateTime(new Date());
        groupUser.setState(null);
        groupMapper.updateByPrimaryKeySelective(groupUser);
        clearGroupCache(groupUser.getGroupId());
        return getChatGroupById(groupUser.getGroupId());
    }

    /**
     * 删除群组
     *
     * @param groupUser
     */
    @Override
    public void delGroup(ChatGroupUser groupUser) {
        NCUtils.nullOrEmptyThrow(groupUser.getGroupId(), new NCException("-1", "群组id不能为空！"));
        ChatGroup exGroup = groupMapper.selectByPrimaryKey(groupUser.getGroupId());
        checkUpdateOrDelete(groupUser, exGroup);

        ChatGroupUser user = groupUserMapper.selectByPrimaryKey(groupUser.getUserId());
        NCUtils.nullOrEmptyThrow(user, new NCException("", "用户不存在！"));
        if (!user.getGroupId().equals(groupUser.getGroupId())) {
            throw new NCException("", "无权解散该群！");
        }
        if (!user.getUserType().equals(NCConstants.INT_1)) {
            throw new NCException("", "只有群主可以解散该群！");
        }
        ChatGroup delGroup = new ChatGroup();
        delGroup.setGroupId(groupUser.getGroupId());
        delGroup.setUpdateTime(new Date());
        delGroup.setState(NCConstants.INT_NEGATIVE_1);
        groupMapper.updateByPrimaryKeySelective(groupUser);
        clearGroupCache(groupUser.getGroupId());
    }

    /**
     * 根据群id获取群信息
     *
     * @param groupId
     * @return
     */
    @Override
    public ChatGroup getChatGroupById(Long groupId) {
        String groupKey = ChatConstants.CHAT_GROUP + groupId;
        ChatGroup chatGroup = redisUtils.get(groupKey, ChatGroup.class);
        if (chatGroup == null) {
            chatGroup = groupMapper.selectByPrimaryKey(groupId);
            int exp = new Random().nextInt(60 * 10);
            redisUtils.set(groupKey, chatGroup, 60 * 60 * 2 + exp);
        }
        NCUtils.nullOrEmptyThrow(chatGroup, new NCException("", "该群不存在！"));
        return chatGroup;
    }

    @Override
    public void clearGroupCache(Long groupId) {
        redisUtils.delete(ChatConstants.CHAT_GROUP + groupId);
    }

    /**
     * 校验修改和删除群信息
     *
     * @param groupUser
     * @param exGroup
     */
    private void checkUpdateOrDelete(ChatGroupUser groupUser, ChatGroup exGroup) {
        NCUtils.nullOrEmptyThrow(exGroup, new NCException("", "群组不存在！"));
        Integer groupType = exGroup.getGroupType();
        if (NCConstants.INT_4 > groupType) {
            NCUtils.nullOrEmptyThrow(groupUser.getTenantId(), new NCException("", "租户id不能为空"));
            if (!groupUser.getTenantId().equals(exGroup.getTenantId())) {
                throw new NCException("", "群组不存在！");
            }
            if (NCConstants.INT_2.equals(groupType)) {
                NCUtils.nullOrEmptyThrow(groupUser.getOrgId(), new NCException("", "组织机构id不能为空"));
                if (!groupUser.getOrgId().equals(exGroup.getOrgId())) {
                    throw new NCException("", "群组不存在！");
                }
            }
        }
        groupUser.setTenantId(null);
        groupUser.setOrgId(null);
        // 辩论群
        if (NCConstants.INT_5.equals(groupType)) {
            NCUtils.nullOrEmptyThrow(groupUser.getDebateId(), new NCException("", "群组id必填！"));
            if (!groupUser.getDebateId().equals(exGroup.getDebateId())) {
                throw new NCException("", "群组不存在！");
            }
        }
    }
}
