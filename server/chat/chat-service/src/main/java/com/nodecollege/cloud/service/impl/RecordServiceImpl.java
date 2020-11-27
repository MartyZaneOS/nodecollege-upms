package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.ChatFriend;
import com.nodecollege.cloud.common.model.po.ChatGroup;
import com.nodecollege.cloud.common.model.po.ChatGroupUser;
import com.nodecollege.cloud.common.model.po.ChatRecord;
import com.nodecollege.cloud.common.model.vo.ChatActiveData;
import com.nodecollege.cloud.common.utils.DateUtils;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.ChatGroupMapper;
import com.nodecollege.cloud.dao.mapper.ChatRecordMapper;
import com.nodecollege.cloud.feign.UserClient;
import com.nodecollege.cloud.service.FriendService;
import com.nodecollege.cloud.service.GroupService;
import com.nodecollege.cloud.service.GroupUserService;
import com.nodecollege.cloud.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author LC
 * @date 2020/2/29 20:30
 */
@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private ChatRecordMapper recordMapper;

    @Autowired
    private ChatGroupMapper groupMapper;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private FriendService friendService;

    /**
     * 查询活跃聊天列表
     *
     * @param queryVO
     * @return
     */
    @Override
    public List<ChatActiveData> getActiveChat(QueryVO<Long> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData(), new NCException("", "用户id不能为空！"));
        NCUtils.nullOrEmptyThrow(queryVO.getPageNum(), new NCException("", "分页号不能为空！"));
        NCUtils.nullOrEmptyThrow(queryVO.getPageSize(), new NCException("", "分页大小不能为空！"));
        PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<ChatActiveData> dataList = recordMapper.getActiveChatList(queryVO.getData());
        if (dataList.isEmpty()) {
            return dataList;
        }
        List<Long> friendIdList = new ArrayList<>(dataList.size());
        List<Long> groupIdList = new ArrayList<>(dataList.size());
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getChatActiveId().indexOf("f") != -1) {
                dataList.get(i).setFriendId(Long.valueOf(dataList.get(i).getChatActiveId().substring(1)));
                friendIdList.add(dataList.get(i).getFriendId());
            } else {
                dataList.get(i).setGroupId(Long.valueOf(dataList.get(i).getChatActiveId().substring(1)));
                groupIdList.add(dataList.get(i).getGroupId());
            }
        }
        List<ChatFriend> friendList = new ArrayList<>();
        if (!friendIdList.isEmpty()) {
            QueryVO<ChatFriend> queryFriend = new QueryVO<>(new ChatFriend());
            queryFriend.getData().setUserId(queryVO.getData());
            queryFriend.setLongList(friendIdList);
            friendList = friendService.getFriendList(queryFriend);
        }
        List<ChatGroupUser> groupList = new ArrayList<>();
        if (!groupIdList.isEmpty()) {
            QueryVO<ChatGroupUser> queryGroup = new QueryVO<>(new ChatGroupUser());
            queryGroup.getData().setUserId(queryVO.getData());
            queryGroup.setLongList(groupIdList);
            groupList = groupMapper.selectGroupListByGroupUser(queryGroup.toMap());
        }
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = 0; j < friendList.size(); j++) {
                if (friendList.get(j).getFriendId().equals(dataList.get(i).getFriendId())) {
                    dataList.get(i).setChatName(friendList.get(j).getNickname());
                    dataList.get(i).setChatType("f");
                    dataList.get(i).setAvatar(friendList.get(j).getAvatar());
                    dataList.get(i).setAvatarThumb(friendList.get(j).getAvatarThumb());
                    break;
                }
            }
            for (int j = 0; j < groupList.size(); j++) {
                if (groupList.get(j).getGroupId().equals(dataList.get(i).getGroupId())) {
                    dataList.get(i).setChatName(groupList.get(j).getGroupName());
                    dataList.get(i).setChatType("g");
                    dataList.get(i).setGroupType(groupList.get(j).getGroupType());
                    break;
                }
            }
        }
        return dataList;
    }

    /**
     * 查询聊天历史记录
     */
    @Override
    public NCResult<ChatRecord> getRecord(QueryVO<ChatRecord> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO, new NCException("-1", "参数缺失！"));
        NCUtils.nullOrEmptyThrow(queryVO.getUserId(), new NCException("-1", "参数缺失！"));
        NCUtils.nullOrEmptyThrow(queryVO.getData(), new NCException("-1", "参数缺失！"));

        if (queryVO.getData().getGroupId() == null && queryVO.getData().getReceiveUserId() == null) {
            throw new NCException("-1", "参数缺失！");
        }
        if (queryVO.getData().getGroupId() != null) {
            List<ChatGroupUser> userList = groupUserService.getGroupUser(queryVO.getData().getGroupId());
            boolean has = false;
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUserId().equals(queryVO.getUserId())) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                throw new NCException("-1", "无查询该群记录权限！");
            }
        }
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<ChatRecord> dataList = recordMapper.selectRecordList(queryVO.toMap());
        long total = page.getTotal();
        if (queryVO.getPageNum() * queryVO.getPageSize() >= total) {
            // 查询到最后一条记录了 返回一条空数据 表示以查完
            ChatRecord endRecord = new ChatRecord();
            endRecord.setReceiveUserId(queryVO.getData().getReceiveUserId());
            endRecord.setSendUserId(queryVO.getUserId());
            endRecord.setGroupId(queryVO.getData().getGroupId());
            endRecord.setRecordType(queryVO.getData().getReceiveUserId() != null ? 2 : 1);
            endRecord.setRecordId(-1L);
            endRecord.setSendTime(DateUtils.parseTime("2010-01-01 00:00:00"));
            endRecord.setReceiveTime(DateUtils.parseTime("2010-01-01 00:00:00"));
            endRecord.setReceiveState(NCConstants.INT_2);
            dataList.add(endRecord);
        }
        return NCResult.ok(dataList, total);
    }

    /**
     * 添加消息
     */
    @Override
    public ChatRecord addRecord(ChatRecord record) {
        NCUtils.nullOrEmptyThrow(record.getSendUserId(), new NCException("", "发送用户id不能为空！"));
        NCUtils.nullOrEmptyThrow(record.getContentType(), new NCException("", "消息内容类型不能为空！"));
        NCUtils.nullOrEmptyThrow(record.getRecordType(), new NCException("", "消息类型不能为空！"));
        // 群组聊天信息
        if (NCUtils.equals(record.getRecordType(), NCConstants.INT_1)) {
            NCUtils.nullOrEmptyThrow(record.getGroupId(), new NCException("", "聊天群消息 群id不能空！"));
            ChatGroup exGroup = groupService.getChatGroupById(record.getGroupId());
        } else if (NCUtils.equals(record.getRecordType(), NCConstants.INT_2)) {
            NCUtils.nullOrEmptyThrow(record.getReceiveUserId(), new NCException("", "接收人id不能为空！"));
//            NCResult<OperateUser> exUser = userClient.getUserByUserId(record.getReceiveUserId());
//            exUser.checkErrorThrow();
        }
        record.setReceiveState(NCConstants.INT_1);
        record.setSendTime(new Date());
        record.setReceiveTime(null);
        record.setState(NCConstants.INT_1);
        recordMapper.insertSelective(record);
        return record;
    }

    /**
     * 读取消息
     */
    @Override
    public ChatRecord readRecord(ChatRecord record) {
        NCUtils.nullOrEmptyThrow(record.getRecordId(), new NCException("", "消息id不能为空！"));
        NCUtils.nullOrEmptyThrow(record.getReceiveUserId(), new NCException("", "接收用户id不能为空！"));
        record.setReceiveState(NCConstants.INT_2);
        record.setReceiveTime(new Date());
        record.setState(null);
        recordMapper.updateByPrimaryKeySelective(record);
        record = recordMapper.selectByPrimaryKey(record.getRecordId());
        return record;
    }

    /**
     * 撤回消息
     */
    @Override
    public ChatRecord withdrawRecord(ChatRecord record) {
        NCUtils.nullOrEmptyThrow(record.getRecordId(), new NCException("", "消息id不能为空！"));
        NCUtils.nullOrEmptyThrow(record.getSendUserId(), new NCException("", "接收用户id不能为空！"));
        record.setReceiveUserId(null);
        record.setReceiveState(null);
        record.setReceiveTime(null);
        record.setState(NCConstants.INT_2);
        recordMapper.updateByPrimaryKeySelective(record);
        return record;
    }
}
