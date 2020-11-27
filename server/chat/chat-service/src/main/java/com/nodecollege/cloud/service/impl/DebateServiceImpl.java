package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.DebateDTO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.*;
import com.nodecollege.cloud.dao.mapper.ChatDebateMapper;
import com.nodecollege.cloud.dao.mapper.ChatDebateRecordMapper;
import com.nodecollege.cloud.dao.mapper.ChatDebateRecordUpMapper;
import com.nodecollege.cloud.dao.mapper.ChatDebateRelationMapper;
import com.nodecollege.cloud.feign.UserClient;
import com.nodecollege.cloud.feign.WorldTreeClient;
import com.nodecollege.cloud.service.DebateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.nodecollege.cloud.common.ChatConstants.*;

/**
 * 辩论堂
 *
 * @author LC
 * @date 2020/4/19 14:13
 */
@Service
public class DebateServiceImpl implements DebateService {

    private static final Logger logger = LoggerFactory.getLogger(DebateServiceImpl.class);

    private static final ExecutorService NC_AOP_EXECUTOR = ThreadPoolFactory.getInstance();

    @Autowired
    private ChatDebateMapper debateMapper;

    @Autowired
    private ChatDebateRecordMapper recordMapper;

    @Autowired
    private ChatDebateRecordUpMapper recordUpMapper;

    @Autowired
    private ChatDebateRelationMapper relationMapper;

    @Autowired
    private WorldTreeClient worldTreeClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加辩论堂
     */
    @Override
    public ChatDebate addDebate(ChatDebate debate) {
        NCUtils.nullOrEmptyThrow(debate, "", "参数缺失");
        NCUtils.nullOrEmptyThrow(debate.getUserId(), "", "用户id不能为空");
        NCUtils.nullOrEmptyThrow(debate.getDebateType(), "", "辩论堂类型不能为空");
        NCUtils.nullOrEmptyThrow(debate.getOriginalJson(), "", "辩论堂原始内容不能为空");
        NCUtils.nullOrEmptyThrow(debate.getUpdateJson(), "", "辩论堂更新内容不能为空");
        NCUtils.nullOrEmptyThrow(debate.getUuid(), "", "随机字符串不能为空");
        debate.setResult(2);
        debate.setSupportNum(0);
        debate.setRefuseNum(0);
        debate.setCreateTime(new Date());
        debateMapper.insertSelective(debate);
        return debate;
    }

    /**
     * 查询辩论堂列表 分页
     */
    @Override
    public NCResult<ChatDebate> getDebateList(QueryVO<ChatDebate> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        if (queryVO.isSort()) {
            page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
        }
        List<ChatDebate> list = debateMapper.selectListByMap(queryVO.toMap());
        return NCResult.ok(list, page.getTotal());
    }

    /**
     * 查询辩论堂缓存
     */
    @Override
    public ChatDebate getDebateCache(Long debateId) {
        ChatDebate debate = redisUtils.get(CHAT_DEBATE_CACHE + debateId, ChatDebate.class);
        if (debate == null) {
            debate = debateMapper.selectByPrimaryKey(debateId);
            clearDebateCache(debateId);
            redisUtils.increment(CHAT_DEBATE_SUPPORT_CACHE + debateId, debate.getSupportNum());
            redisUtils.increment(CHAT_DEBATE_REFUSE_CACHE + debateId, debate.getRefuseNum());
            redisUtils.set(CHAT_DEBATE_CACHE + debateId, debate, new Random().nextInt(60 * 10));
        }
        return debate;
    }

    /**
     * 清除辩论堂缓存
     */
    @Override
    public void clearDebateCache(Long debateId) {
        redisUtils.delete(CHAT_DEBATE_CACHE + debateId);
        redisUtils.delete(CHAT_DEBATE_SUPPORT_CACHE + debateId);
        redisUtils.delete(CHAT_DEBATE_REFUSE_CACHE + debateId);
    }

    /**
     * 批量添加节点辩论堂关系列表
     */
    @Override
    public void addRelation(List<ChatDebateRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) return;
        for (int i = 0; i < relationList.size(); i++) {
            relationList.get(i).setFinish(1);
            relationList.get(i).setCreateTime(new Date());
            relationMapper.insert(relationList.get(i));
        }
    }

    /**
     * 根据节点信息查询关联辩论堂信息
     */
    @Override
    public NCResult<ChatDebateRelation> getRelation(QueryVO<ChatDebateRelation> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO, "", "参数缺失！");
        NCUtils.nullOrEmptyThrow(queryVO.getData(), "", "参数缺失！");
        if (queryVO.getData().getNodeId() == null && queryVO.getData().getDebateId() == null) {
            throw new NCException("", "节点id或者辩论堂id不能为空！");
        }
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        if (queryVO.isSort()) {
            page.setOrderBy(queryVO.getSortKey() + " " + queryVO.getSortDirection());
        }
        List<ChatDebateRelation> list = relationMapper.selectListByMap(queryVO.toMap());
        return NCResult.ok(list, page.getTotal());
    }

    /**
     * 添加辩论记录
     */
    @Override
    public ChatDebateRecord addRecord(ChatDebateRecord record) {
        NCUtils.nullOrEmptyThrow(record, "", "参数缺失！");
        NCUtils.nullOrEmptyThrow(record.getDebateId(), "", "辩论堂id不能为空！");
        NCUtils.nullOrEmptyThrow(record.getUserId(), "", "用户id不能为空！");
        NCUtils.nullOrEmptyThrow(record.getSupport(), "", "是否支持不能为空！");
        NCUtils.nullOrEmptyThrow(record.getRecord(), "", "辩论记录不能为空！");

        ChatDebate debate = getDebateCache(record.getDebateId());
        NCUtils.nullOrEmptyThrow(debate, "", "辩论堂不存在！");
        if (debate.getResult() != 2) {
            throw new NCException("", "该辩论堂已结束");
        }

        record.setSendTime(new Date());
        record.setSupportNum(0);
        record.setState(1);
        recordMapper.insertSelective(record);
        if (record.getSupport() == 1) {
            Long supportNum = redisUtils.increment(CHAT_DEBATE_SUPPORT_CACHE + record.getDebateId(), 1);
            debate.setSupportNum(supportNum.intValue());
        } else {
            Long refuseNum = redisUtils.increment(CHAT_DEBATE_REFUSE_CACHE + record.getDebateId(), 1);
            debate.setRefuseNum(refuseNum.intValue());
        }
        redisUtils.set(CHAT_DEBATE_CACHE + record.getDebateId(), debate, new Random().nextInt(60 * 10));
        return record;
    }

    /**
     * 查询辩论记录列表
     */
    @Override
    public NCResult<ChatDebateRecord> getRecordList(QueryVO<ChatDebateRecord> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO, "", "参数缺失");
        NCUtils.nullOrEmptyThrow(queryVO.getData(), "", "参数缺失");
        NCUtils.nullOrEmptyThrow(queryVO.getData().getDebateId(), "", "辩论堂id不能为空");
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<ChatDebateRecord> list = recordMapper.selectListByMap(queryVO.toMap());
        long total = page.getTotal();
        if (queryVO.getPageNum() * queryVO.getPageSize() >= total) {
            // 查询到最后一条记录了 返回一条空数据 表示以查完
            ChatDebateRecord endRecord = new ChatDebateRecord();
            endRecord.setDebateId(queryVO.getData().getDebateId());
            endRecord.setDebateRecordId(-1L);
            endRecord.setSendTime(DateUtils.parseTime("2010-01-01 00:00:00"));
            list.add(endRecord);
        }
        return NCResult.ok(list, page.getTotal());
    }

    /**
     * 点赞记录
     */
    @Override
    public ChatDebateRecordUp addRecordUp(ChatDebateRecordUp recordUp) {
        NCUtils.nullOrEmptyThrow(recordUp, "", "参数缺失");
        NCUtils.nullOrEmptyThrow(recordUp.getDebateId(), "", "辩论id不能为空");
        NCUtils.nullOrEmptyThrow(recordUp.getDebateRecordId(), "", "辩论记录id不能为空");
        NCUtils.nullOrEmptyThrow(recordUp.getUserId(), "", "用户id不能为空");

        ChatDebateRecordUp exist = recordUpMapper.selectExist(recordUp);
        if (exist != null) {
            return null;
        }

        ChatDebate debate = getDebateCache(recordUp.getDebateId());
        NCUtils.nullOrEmptyThrow(debate, "", "辩论堂不存在！");
        if (debate.getResult() != 2) {
            throw new NCException("", "该辩论堂已结束");
        }

        ChatDebateRecord record = recordMapper.selectByPrimaryKey(recordUp.getDebateRecordId());
        NCUtils.nullOrEmptyThrow(record, "", "该辩论记录不存在！");
        if (record.getState() != 1) {
            throw new NCException("", "该辩论记录已撤回");
        }
        recordUpMapper.insert(recordUp);
        ChatDebateRecord update = new ChatDebateRecord();
        update.setDebateRecordId(record.getDebateRecordId());
        update.setSupportNum(record.getSupportNum() + 1);
        recordMapper.updateByPrimaryKeySelective(update);
        if (record.getSupport() == 1) {
            Long supportNum = redisUtils.increment(CHAT_DEBATE_SUPPORT_CACHE + record.getDebateId(), 1);
            debate.setSupportNum(supportNum.intValue());
        } else {
            Long refuseNum = redisUtils.increment(CHAT_DEBATE_REFUSE_CACHE + record.getDebateId(), 1);
            debate.setRefuseNum(refuseNum.intValue());
        }
        redisUtils.set(CHAT_DEBATE_CACHE + record.getDebateId(), debate, new Random().nextInt(60 * 10));
        return recordUp;
    }

    /**
     * 删除点赞记录
     */
    @Override
    public ChatDebateRecordUp delRecordUp(ChatDebateRecordUp recordUp) {
        NCUtils.nullOrEmptyThrow(recordUp, "", "参数缺失");
        NCUtils.nullOrEmptyThrow(recordUp.getDebateId(), "", "辩论id不能为空");
        NCUtils.nullOrEmptyThrow(recordUp.getDebateRecordId(), "", "辩论记录id不能为空");
        NCUtils.nullOrEmptyThrow(recordUp.getUserId(), "", "用户id不能为空");
        ChatDebateRecordUp exist = recordUpMapper.selectExist(recordUp);
        if (exist != null) {
            ChatDebateRecord record = recordMapper.selectByPrimaryKey(recordUp.getDebateRecordId());
            NCUtils.nullOrEmptyThrow(record, "", "该辩论记录不存在！");
            if (record.getState() != 1) {
                throw new NCException("", "该辩论记录已撤回");
            }
            recordUpMapper.deleteByPrimaryKey(exist.getDebateRecordUpId());
            if (record.getSupport() == 1) {
                redisUtils.increment(CHAT_DEBATE_SUPPORT_CACHE + record.getDebateId(), -1);
            } else {
                redisUtils.increment(CHAT_DEBATE_REFUSE_CACHE + record.getDebateId(), -1);
            }
            ChatDebateRecord update = new ChatDebateRecord();
            update.setDebateRecordId(record.getDebateRecordId());
            update.setSupportNum(record.getSupportNum() - 1);
            recordMapper.updateByPrimaryKeySelective(update);
            return exist;
        } else {
            return null;
        }
    }

    /**
     * 接口添加辩论堂及关系
     */
    @Override
    @Transactional(readOnly = false)
    public void addDebateApi(DebateDTO debateDTO) {
        ChatDebate debate = debateDTO.getDebate();
        debate = addDebate(debate);
        List<ChatDebateRelation> relationList = debateDTO.getRelationList();
        for (int i = 0; i < relationList.size(); i++) {
            relationList.get(i).setDebateId(debate.getDebateId());
        }
        addRelation(relationList);
    }

    /**
     * 获取用户信息
     */
    @Override
    public List<OperateUser> getUserInfo(QueryVO queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getLongList(), "", "参数缺失");
        NCResult<OperateUser> userNCResult = userClient.getUserListByQuery(queryVO);
        userNCResult.checkErrorThrow();
        return userNCResult.getRows();
    }

    /**
     * 获取点赞用户列表
     */
    @Override
    public List<OperateUser> getUpUserList(ChatDebateRecordUp recordUp) {
        NCUtils.nullOrEmptyThrow(recordUp.getDebateId(), "", "参数缺失");
        NCUtils.nullOrEmptyThrow(recordUp.getDebateRecordId(), "", "参数缺失");

        List<ChatDebateRecordUp> upList = recordUpMapper.selectList(recordUp);
        Set<Long> userIdList = upList.stream().map(item -> item.getUserId()).collect(Collectors.toSet());

        QueryVO queryUser = new QueryVO();
        queryUser.setLongList(new ArrayList<>(userIdList));
        NCResult<OperateUser> userNCResult = userClient.getUserListByQuery(queryUser);
        userNCResult.checkErrorThrow();
        return userNCResult.getRows();
    }

    /**
     * 获取我点赞的辩论列表
     */
    @Override
    public List<ChatDebateRecordUp> getMyUpList(ChatDebateRecordUp recordUp) {
        NCUtils.nullOrEmptyThrow(recordUp.getDebateId(), "", "参数缺失！");
        NCUtils.nullOrEmptyThrow(recordUp.getUserId(), "", "参数缺失！");
        return recordUpMapper.selectList(recordUp);
    }

    /**
     * 刷新在缓存中的辩论堂支持数据
     * 每隔5分钟刷新一次
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void refreshNum() {
        logger.info("#############   刷新在缓存中的辩论堂支持数据  启动   #############");
        Set<String> debateIdSet = redisTemplate.keys(CHAT_DEBATE_CACHE + "*");
        if (debateIdSet == null || debateIdSet.isEmpty()) return;
        List<String> debateIdList = new ArrayList<>(debateIdSet);
        for (int i = 0; i < debateIdList.size(); i++) {
            Long debateId = Long.valueOf(debateIdList.get(i).substring(CHAT_DEBATE_CACHE.length()));
            refreshNum(debateId);
        }
        logger.info("#############   刷新在缓存中的辩论堂支持数据  结束   #############");
    }

    /**
     * 刷新缓存中的辩论堂支持数据
     */
    @Async("asyncPoolTaskExecutor")
    public void refreshNum(Long debateId) {
        try {
            if (redisLock.tryLock("debate:" + debateId, 30)) {
                ChatDebate debate = debateMapper.selectByPrimaryKey(debateId);
                if (debate == null) return;
                debate.setSupportNum(0);
                debate.setRefuseNum(0);
                QueryVO<ChatDebateRecord> queryRecord = new QueryVO<>(new ChatDebateRecord());
                queryRecord.getData().setDebateId(debateId);
                List<ChatDebateRecord> recordList = recordMapper.selectListByMap(queryRecord.toMap());
                if (recordList.isEmpty()) return;
                for (int j = 0; j < recordList.size(); j++) {
                    ChatDebateRecordUp queryUp = new ChatDebateRecordUp();
                    queryUp.setDebateId(debateId);
                    queryUp.setDebateRecordId(recordList.get(j).getDebateRecordId());
                    Integer count = recordUpMapper.selectCount(queryUp);
                    recordList.get(j).setSupportNum(count);
                    recordMapper.updateByPrimaryKeySelective(recordList.get(j));
                    if (recordList.get(j).getSupport() == 1) {
                        debate.setSupportNum(debate.getSupportNum() + count);
                    } else {
                        debate.setRefuseNum(debate.getRefuseNum() + count);
                    }
                }
                debateMapper.updateByPrimaryKeySelective(debate);
                clearDebateCache(debateId);
            }
        } finally {
            redisLock.releaseLock("debate:" + debateId, 30);
        }
    }

    /**
     * 辩论堂结束定时任务
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void debateSync() {
        List<ChatDebate> list = debateMapper.selectEndDebateList(new Date());
        if (list.size() > 0) {
            logger.info("检测辩论堂已经到达结束时间且状态还是辩论中的数据共[" + list.size() + "]条。");
            for (int i = 0; i < list.size(); i++) {
                refreshNum(list.get(i).getDebateId());
            }
        }
        for (ChatDebate debate : list) {
            debateHandle(debate);
        }
    }

    /**
     * 刷新缓存中的辩论堂支持数据
     */
    @Async("asyncPoolTaskExecutor")
    public void debateHandle(ChatDebate debate) {
        try {
            if (redisLock.tryLock("debate:" + debate.getDebateId(), 30)) {
                debate = debateMapper.selectByPrimaryKey(debate.getDebateId());
                ChatDebate update = new ChatDebate();
                update.setDebateId(debate.getDebateId());
                // 支持率大于等于反对率 执行改变
                if (debate.getSupportNum().compareTo(debate.getRefuseNum()) >= 0) {
                    NCResult res = worldTreeClient.debateHandle(debate);
                    if (res.getSuccess()) {
                        update.setResult(1);
                        update.setResultMsg("支持数大于反对数，辩论成功！");
                    } else if ("80000007".equals(res.getCode())) {
                        logger.info("服务建调用异常直接返回，等待下次处理！");
                        return;
                    } else {
                        update.setResult(-1);
                        update.setResultMsg(res.getMessage());
                    }
                } else {
                    update.setResult(0);
                    update.setResultMsg("支持数小于反对数，辩论失败！");
                }
                debateMapper.updateByPrimaryKeySelective(update);
                relationMapper.updateFinish(debate.getDebateId());
            }
        } finally {
            redisLock.releaseLock("debate:" + debate.getDebateId(), 30);
        }
    }
}
