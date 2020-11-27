package com.nodecollege.cloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.nodecollege.cloud.common.constants.ConfigConstants;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.PasswordUtil;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.common.utils.RegularExpUtils;
import com.nodecollege.cloud.dao.mapper.TenantMemberMapper;
import com.nodecollege.cloud.dao.mapper.TenantMemberOrgMapper;
import com.nodecollege.cloud.dao.mapper.TenantMemberOrgRoleMapper;
import com.nodecollege.cloud.dao.mapper.TenantOrgMapper;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.ConfigService;
import com.nodecollege.cloud.service.MemberService;
import com.nodecollege.cloud.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * userService
 *
 * @author LC
 * @date 2019/6/13 15:49
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private TenantMemberMapper memberMapper;

    @Autowired
    private TenantOrgMapper orgMapper;

    @Autowired
    private TenantClient tenantClient;

    @Autowired
    private TenantMemberOrgMapper memberOrgMapper;

    @Autowired
    private TenantMemberOrgRoleMapper memberOrgRoleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询所有用户信息
     */
    @Override
    public List<TenantMember> list(QueryVO<TenantMember> query) {
        NCUtils.nullOrEmptyThrow(query.getData().getTenantId());
        return memberMapper.selectMemberListByMap(query.toMap());
    }

    /**
     * 邀请成员
     */
    @Override
    public OperateUserInvite inviteMember(OperateUserInvite invite) {
        NCUtils.nullOrEmptyThrow(invite.getTenantId());
        NCUtils.nullOrEmptyThrow(invite.getCreateUser());
        NCUtils.nullOrEmptyThrow(invite.getUserName());
        NCUtils.nullOrEmptyThrow(invite.getTelephone());

        NCResult<OperateUser> userResult = tenantClient.inviteUser(invite);
        if (!userResult.getSuccess()) {
            throw new NCException("", "系统异常！");
        } else if (userResult.getRows() == null || userResult.getRows().isEmpty()) {
            return null;
        } else {
            OperateUser user = userResult.getRows().get(0);
            TenantMember member = new TenantMember();
            member.setTenantId(user.getTenantId());
            member.setUserId(user.getUserId());
            member.setAccount(user.getTelephone());
            member.setNickname(user.getNickname());
            member.setTelephone(invite.getTelephone());
            addMember(member);
        }
        return invite;
    }

    @Override
    public void inviteMemberSuccess(OperateUser user) {
        TenantMember member = new TenantMember();
        member.setUserId(user.getUserId());
        member.setTenantId(user.getTenantId());
        member.setAccount(user.getTelephone());
        member.setTelephone(user.getTelephone());
        member.setNickname(user.getNickname());
        addMember(member);
    }

    /**
     * 添加成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantMember addMember(TenantMember member) {
        NCUtils.nullOrEmptyThrow(member.getTenantId());
        NCUtils.nullOrEmptyThrow(member.getUserId());
        NCUtils.nullOrEmptyThrow(member.getAccount());

        // 1 校验手机号是否已经注册
        QueryVO<TenantMember> query = new QueryVO<>(new TenantMember());
        query.getData().setTenantId(member.getTenantId());
        query.getData().setAccount(member.getAccount());
        List<TenantMember> result = memberMapper.selectMemberListByMap(query.toMap());
        NCUtils.notNullOrNotEmptyThrow(result, "-1", "此账号已注册");

        // 2 校验手机号是否注册
        query.getData().setAccount(null);
        query.getData().setTelephone(member.getTelephone());
        result = memberMapper.selectMemberListByMap(query.toMap());
        NCUtils.notNullOrNotEmptyThrow(result, "-1", "此手机号已注册");

        // 3 查询密码配置信息
        OperateConfig queryConfig = new OperateConfig();
        queryConfig.setTenantId(member.getTenantId());
        queryConfig.setConfigUsage(7);
        queryConfig.setConfigCode(ConfigConstants.DEFAULT_PWD);
        OperateConfig config = configService.getConfigCacheByCode(queryConfig);
        String salt = PasswordUtil.getRandomSalt();
        String password = PasswordUtil.md5(config != null ? config.getConfigValue() : "a123456", salt);

        // 4 保存用户信息
        member.setSalt(salt);
        member.setPassword(password);
        member.setCreateTime(new Date());
        member.setState(1);
        member.setFirstLogin(1);
        member.setShowAllOrg(true);
        member.setShowAllRole(true);
        memberMapper.insertSelective(member);

        // 5 保存用户机构信息
        queryConfig.setConfigCode(ConfigConstants.DEFAULT_MEMBER_ORG);
        config = configService.getConfigCacheByCode(queryConfig);
        if (config != null) {
            List<String> orgs = JSON.parseArray(config.getConfigValue(), String.class);
            QueryVO<TenantOrg> queryOrg = new QueryVO<>(new TenantOrg());
            queryOrg.getData().setTenantId(member.getTenantId());
            List<TenantOrg> orgList = orgMapper.selectListByMap(queryOrg.toMap());
            List<String> exOrg = orgList.stream().map(item -> item.getOrgCode()).collect(Collectors.toList());
            for (String orgCode : orgs) {
                if (exOrg.contains(orgCode)) {
                    BindVO bindVO = new BindVO();
                    bindVO.setTenantId(member.getTenantId());
                    bindVO.setSourceIds(Arrays.asList(member.getMemberId()));
                    bindVO.setTargetCodes(Arrays.asList(orgCode));
                    bindMemberOrg(bindVO);
                }
            }
        }
        return member;
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NCLoginUserVO updateMember(TenantMember user, String userAccessToken) {
        NCUtils.nullOrEmptyThrow(user.getTenantId());
        NCUtils.nullOrEmptyThrow(user.getMemberId());

        TenantMember oldMember = memberMapper.selectByPrimaryKey(user.getMemberId());
        NCUtils.nullOrEmptyThrow(oldMember, "", "该用户不存在！");

        if (!user.getTenantId().equals(oldMember.getTenantId())) {
            throw new NCException("", "不能修改该用户数据！");
        }

        if (StringUtils.isNotBlank(user.getTelephone()) && !RegularExpUtils.checkTelephone(user.getTelephone())) {
            throw new NCException("-1", "电话格式不正确！");
        }

        QueryVO<TenantMember> queryMember = new QueryVO<>(new TenantMember());
        queryMember.setTenantId(user.getTenantId());

        if (StringUtils.isNotBlank(user.getAccount())) {
            queryMember.getData().setAccount(user.getAccount());
            List<TenantMember> exList = memberMapper.selectMemberListByMap(queryMember.toMap());
            if (exList.size() == 1 && !exList.get(0).getMemberId().equals(user.getMemberId())) {
                throw new NCException("-1", "该账号已存在！");
            }
        }

        if (StringUtils.isNotBlank(user.getTelephone())) {
            queryMember.setData(new TenantMember());
            queryMember.getData().setTelephone(user.getTelephone());
            List<TenantMember> exList = memberMapper.selectMemberListByMap(queryMember.toMap());
            if (exList.size() == 1 && !exList.get(0).getMemberId().equals(user.getMemberId())) {
                throw new NCException("-1", "该手机号已存在！");
            }
        }

        TenantMember updateMember = new TenantMember();
        updateMember.setMemberId(user.getMemberId());
        updateMember.setNickname(user.getNickname());
        updateMember.setAccount(user.getAccount());
        updateMember.setTelephone(user.getTelephone());
        updateMember.setEmail(user.getEmail());
        updateMember.setSex(user.getSex());
        memberMapper.updateByPrimaryKeySelective(updateMember);
        if (StringUtils.isNotBlank(userAccessToken)) {
            String key = RedisConstants.MEMBER_LOGIN_INFO + userAccessToken;
            NCLoginUserVO loginMember = redisUtils.get(key, NCLoginUserVO.class);
            Long expire = redisUtils.getExpire(key);
            if (loginMember != null && expire != null) {
                loginMember = new NCLoginUserVO(getMemberPower(user));
                loginMember.setLoginId(user.getMemberId());
                loginMember.setAccount(user.getAccount());
                loginMember.setNickname(StringUtils.isBlank(user.getNickname()) ? user.getAccount() : user.getNickname());
                loginMember.setAvatar(user.getAvatar());
                loginMember.setAccessToken(userAccessToken);
                loginMember.setUuid(NCUtils.getUUID());
                loginMember.setExpire(expire.intValue());
                loginMember.setTenantId(user.getTenantId());
                loginMember.setAvatar(user.getAvatar());
                loginMember.setAvatarThumb(user.getAvatarThumb());

                // 初始化用户信息数据权限信息
                redisUtils.set(key, loginMember, expire);

                return loginMember;
            }
        }
        return null;
    }

    /**
     * 删除用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMember(TenantMember member) {
        NCUtils.nullOrEmptyThrow(member.getTenantId());
        NCUtils.nullOrEmptyThrow(member.getMemberId());

        TenantMember oldMember = memberMapper.selectByPrimaryKey(member.getMemberId());
        NCUtils.nullOrEmptyThrow(oldMember, "-1", "用户删除失败，根据id未查询到用户信息！");

        if (oldMember.getState() == 0) {
            throw new NCException("-1", "该用户不能删除！");
        }

        if (!member.getTenantId().equals(oldMember.getTenantId())) {
            throw new NCException("", "无权删除该用户数据！");
        }
        // 成员机构角色信息
        TenantMemberOrgRole queryRole = new TenantMemberOrgRole();
        queryRole.setTenantId(member.getTenantId());
        queryRole.setMemberId(member.getMemberId());
        List<TenantMemberOrgRole> exRole = memberOrgRoleMapper.selectList(queryRole);
        for (TenantMemberOrgRole role : exRole) {
            memberOrgRoleMapper.deleteByPrimaryKey(role.getId());
        }

        // 成员机构信息
        TenantMemberOrg queryOrg = new TenantMemberOrg();
        queryOrg.setTenantId(member.getTenantId());
        queryOrg.setMemberId(member.getMemberId());
        List<TenantMemberOrg> exOrg = memberOrgMapper.selectList(queryOrg);
        for (TenantMemberOrg org : exOrg) {
            memberOrgMapper.deleteByPrimaryKey(org.getId());
        }

        member.setState(NCConstants.INT_NEGATIVE_1);
        memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public void resetPwd(TenantMember user) {
        NCUtils.nullOrEmptyThrow(user.getMemberId());

        TenantMember ex = memberMapper.selectByPrimaryKey(user.getMemberId());
        NCUtils.nullOrEmptyThrow(ex);
        if (!user.getTenantId().equals(ex.getTenantId())) {
            throw new NCException("", "不能重置该成员密码！");
        }

        // 3 查询密码配置信息
        OperateConfig queryConfig = new OperateConfig();
        queryConfig.setTenantId(ex.getTenantId());
        queryConfig.setConfigUsage(7);
        queryConfig.setConfigCode(ConfigConstants.DEFAULT_PWD);
        OperateConfig config = configService.getConfigCacheByCode(queryConfig);
        String salt = PasswordUtil.getRandomSalt();
        String password = PasswordUtil.md5(config != null ? config.getConfigValue() : "a123456", salt);

        TenantMember resetPwd = new TenantMember();
        resetPwd.setMemberId(user.getMemberId());
        resetPwd.setPassword(password);
        resetPwd.setSalt(salt);
        memberMapper.updateByPrimaryKeySelective(resetPwd);
    }

    @Override
    public void updatePwd(Long userId, String password, String newPassword) {
        TenantMember member = memberMapper.selectByPrimaryKey(userId);
        NCUtils.nullOrEmptyThrow(member, "-1", "修改密码失败，根据用户id未获取到用户信息！");
        password = PasswordUtil.md5(password, member.getSalt());
        if (!password.equals(member.getPassword())) {
            throw new NCException("-1", "修改密码失败，旧密码输入错误！");
        }

        // 更新新密码
        member.setSalt(PasswordUtil.getRandomSalt());
        member.setPassword(PasswordUtil.md5(newPassword, member.getSalt()));
        member.setFirstLogin(1);
        memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public NCLoginUserVO changeDefaultOption(NCLoginUserVO loginMember, TenantMember member) {
        TenantMember update = new TenantMember();
        update.setMemberId(member.getMemberId());
        update.setDefaultOrgCode(member.getDefaultOrgCode());
        update.setDefaultRoleCode(member.getDefaultRoleCode());
        update.setShowAllOrg(member.getShowAllOrg());
        update.setShowAllRole(member.getShowAllRole());
        memberMapper.updateByPrimaryKeySelective(update);

        PowerVO powerVO = getMemberPower(member);

        member = memberMapper.selectByPrimaryKey(member.getMemberId());

        NCLoginUserVO memberLoginInfo = new NCLoginUserVO(powerVO);
        memberLoginInfo.setLoginId(member.getMemberId());
        memberLoginInfo.setAccount(member.getAccount());
        memberLoginInfo.setNickname(StringUtils.isBlank(member.getNickname()) ? member.getAccount() : member.getNickname());
        memberLoginInfo.setAvatar(member.getAvatar());
        memberLoginInfo.setAccessToken(loginMember.getAccessToken());
        memberLoginInfo.setUuid(NCUtils.getUUID());
        memberLoginInfo.setTenantId(member.getTenantId());
        memberLoginInfo.setTenantCode(loginMember.getTenantCode());
        memberLoginInfo.setTenantName(loginMember.getTenantName());
        memberLoginInfo.setAvatar(member.getAvatar());
        memberLoginInfo.setAvatarThumb(member.getAvatarThumb());
        Long expire = redisUtils.getExpire(RedisConstants.MEMBER_LOGIN_INFO + loginMember.getAccessToken());
        memberLoginInfo.setExpire(expire != null ? expire.intValue() : 0);

        redisUtils.set(RedisConstants.MEMBER_LOGIN_INFO + loginMember.getAccessToken(), memberLoginInfo, expire != null ? expire.intValue() : 0);
        return memberLoginInfo;
    }

    @Override
    public void bindMemberOrg(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceIds());
        NCUtils.nullOrEmptyThrow(bindVO.getTenantId());
        // 已绑定的数据
        TenantMemberOrg query = new TenantMemberOrg();
        query.setMemberId(bindVO.getSourceIds().get(0));
        query.setTenantId(bindVO.getTenantId());
        List<TenantMemberOrg> exList = memberOrgMapper.selectList(query);

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getOrgCode())) {
                TenantMemberOrgRole queryRole = new TenantMemberOrgRole();
                queryRole.setMemberId(bindVO.getSourceIds().get(0));
                queryRole.setTenantId(bindVO.getTenantId());
                queryRole.setOrgCode(item.getOrgCode());
                List<TenantMemberOrgRole> exRole = memberOrgRoleMapper.selectList(queryRole);
                NCUtils.notNullOrNotEmptyThrow(exRole, "", item.getOrgCode() + "机构存在绑定成员信息!不能删除！");
                memberOrgMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getOrgCode());
            }
        });
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                TenantMemberOrg add = new TenantMemberOrg();
                add.setTenantId(bindVO.getTenantId());
                add.setOrgCode(item);
                add.setMemberId(bindVO.getSourceIds().get(0));
                memberOrgMapper.insert(add);
            }
        });
    }

    @Override
    public List<TenantMember> getMemberListByOrg(QueryVO<TenantMemberOrg> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getTenantId());
        return memberMapper.selectListByOrg(queryVO.toMap());
    }

    @Override
    public List<TenantMember> getMemberListByRoleOrg(TenantMemberOrgRole queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getTenantId());
        return memberMapper.selectListByRoleOrg(queryVO);
    }

    @Override
    public void addMemberOrgRole(TenantMemberOrgRole bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getTenantId());
        NCUtils.nullOrEmptyThrow(bindVO.getMemberId());
        NCUtils.nullOrEmptyThrow(bindVO.getOrgCode());
        NCUtils.nullOrEmptyThrow(bindVO.getRoleCode());
        List<TenantMemberOrgRole> exList = memberOrgRoleMapper.selectList(bindVO);
        NCUtils.notNullOrNotEmptyThrow(exList, "", "用户机构角色已绑定！");
        memberOrgRoleMapper.insert(bindVO);
    }

    @Override
    public void delMemberOrgRole(TenantMemberOrgRole bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getTenantId());
        NCUtils.nullOrEmptyThrow(bindVO.getMemberId());
        NCUtils.nullOrEmptyThrow(bindVO.getOrgCode());
        NCUtils.nullOrEmptyThrow(bindVO.getRoleCode());
        List<TenantMemberOrgRole> exList = memberOrgRoleMapper.selectList(bindVO);
        NCUtils.nullOrEmptyThrow(exList, "", "用户机构角色已解绑！");
        memberOrgRoleMapper.deleteByPrimaryKey(exList.get(0).getId());
    }

    @Override
    public PowerVO getMemberPower(TenantMember user) {
        NCUtils.nullOrEmptyThrow(user.getMemberId());
        NCUtils.nullOrEmptyThrow(user.getTenantId());
        NCUtils.nullOrEmptyThrow(user.getShowAllOrg());
        NCUtils.nullOrEmptyThrow(user.getShowAllRole());
        if (!user.getShowAllRole() && StringUtils.isBlank(user.getDefaultRoleCode())) {
            throw new NCException("", "默认角色必选");
        }
        if (!user.getShowAllOrg() && StringUtils.isBlank(user.getDefaultOrgCode())) {
            throw new NCException("", "默认机构必选");
        }

        TenantMember ex = memberMapper.selectByPrimaryKey(user.getMemberId());
        NCUtils.nullOrEmptyThrow(ex, "", "成员不存在！");

        if (!user.getTenantId().equals(ex.getTenantId())) {
            throw new NCException("", "成员不存在！");
        }

        // 所有角色
        Map<String, TenantOrg> orgMap = new HashMap<>();
        Map<String, TenantRole> roleMap = new HashMap<>();

        // 1 获取基础授权数据
        PowerVO powerVO = getBaseMemberPower(user, orgMap, roleMap);

        OperateTenant queryMenu = new OperateTenant();

        queryMenu.setTenantId(user.getTenantId());
        List<MenuVO> menuList = tenantClient.getTenantMenu(queryMenu).getRows();
        // 2 获取菜单列表
        if (user.getShowAllOrg()) {
            if (user.getShowAllRole()) {
                // 获取 showAllOrg=true、showAllRole=true 菜单树
                getPowerMenuTree(user.getTenantId(), powerVO, roleMap, menuList);
            } else {
                // 获取 showAllOrg=true、showAllRole=false 菜单树
                getTFPowerMenuTree(user.getTenantId(), powerVO, roleMap, menuList);
            }
        } else {
            if (user.getShowAllRole()) {
                // 获取 showAllOrg=false、showAllRole=true 菜单树
                getFTPowerMenuTree(user.getTenantId(), powerVO, roleMap, menuList);
            } else {
                // 获取 showAllOrg=false、showAllRole=false 菜单树
                getFFPowerMenuTree(user.getTenantId(), powerVO, roleMap, menuList);
            }
        }

        // 3 数据权限处理
        handelDataPower(user.getTenantId(), powerVO.getMenuTree());
        powerVO.setMenuTree(MenuVO.getMenuTree(powerVO.getMenuTree()));
        return powerVO;
    }

    /***
     * 基础数据处理
     */
    private PowerVO getBaseMemberPower(TenantMember user, Map<String, TenantOrg> orgMap, Map<String, TenantRole> roleMap) {
        PowerVO powerVO = new PowerVO();
        powerVO.setShowAllOrg(user.getShowAllOrg());
        powerVO.setDefaultOrgCode(user.getDefaultOrgCode());
        powerVO.setShowAllRole(user.getShowAllRole());
        powerVO.setDefaultRoleCode(user.getDefaultRoleCode());
        powerVO.setOrgList(new HashSet<>());
        powerVO.setRoleList(new HashSet<>());
        powerVO.setOrgRoleMap(new HashMap<>());
        powerVO.setRoleOrgMap(new HashMap<>());

        // 查询所有授权机构
        QueryVO<TenantMemberOrg> queryOrg = new QueryVO<>(new TenantMemberOrg());
        queryOrg.getData().setMemberId(user.getMemberId());
        queryOrg.getData().setTenantId(user.getTenantId());
        List<TenantOrg> orgList = orgMapper.selectListByMember(queryOrg.toMap());


        QueryVO<TenantRoleOrg> queryRoleOrg = new QueryVO<>(new TenantRoleOrg());
        queryRoleOrg.getData().setTenantId(user.getTenantId());
        orgList.forEach(org -> {
            orgMap.put(org.getOrgCode(), org);

            // 机构对应的角色列表
            if (!powerVO.getOrgRoleMap().containsKey(org.getOrgCode())) {
                powerVO.getOrgRoleMap().put(org.getOrgCode(), new HashSet<PowerVO.CodeName>());
            }

            // 拥有的所有机构信息
            powerVO.getOrgList().add(new PowerVO.CodeName(org.getOrgCode(), org.getOrgName()));

            // 查询授权机构拥有的角色
            queryRoleOrg.getData().setOrgCode(org.getOrgCode());
            List<TenantRole> roleList = roleService.getRoleListByOrg(queryRoleOrg);
            roleList.forEach(role -> {
                roleMap.put(role.getRoleCode(), role);
                // 角色对应的机构列表
                if (!powerVO.getRoleOrgMap().containsKey(role.getRoleCode())) {
                    powerVO.getRoleOrgMap().put(role.getRoleCode(), new HashSet<>());
                }
                // 拥有的所有角色
                if (role.getRoleType() == 0) {
                    // 组织角色
                    powerVO.getRoleList().add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName()));
                    powerVO.getOrgRoleMap().get(org.getOrgCode()).add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName(), role.getRoleSource()));
                    powerVO.getRoleOrgMap().get(role.getRoleCode()).add(new PowerVO.CodeName(org.getOrgCode(), org.getOrgName()));
                } else {
                    // 组织成员角色
                    TenantMemberOrgRole queryAOR = new TenantMemberOrgRole();
                    queryAOR.setMemberId(user.getMemberId());
                    queryAOR.setRoleCode(role.getRoleCode());
                    queryAOR.setOrgCode(org.getOrgCode());
                    queryAOR.setTenantId(user.getTenantId());
                    List<TenantMember> exMemberList = memberMapper.selectListByRoleOrg(queryAOR);
                    if (!exMemberList.isEmpty()) {
                        powerVO.getRoleList().add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName()));
                        powerVO.getOrgRoleMap().get(org.getOrgCode()).add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName(), role.getRoleSource()));
                        powerVO.getRoleOrgMap().get(role.getRoleCode()).add(new PowerVO.CodeName(org.getOrgCode(), org.getOrgName()));
                    }
                }
            });
        });
        powerVO.getRoleOrgMap().remove(null);
        powerVO.getOrgRoleMap().remove(null);
        return powerVO;
    }

    /**
     * 生成菜单树
     * showAllOrg=true、showAllRole=true
     */
    private void getPowerMenuTree(Long tenantId, PowerVO powerVO, Map<String, TenantRole> roleMap, List<MenuVO> menuList) {
        Map<String, MenuVO> menuMap = new HashMap<>();
        for (Map.Entry<String, Set<PowerVO.CodeName>> entry : powerVO.getOrgRoleMap().entrySet()) {
            Set<PowerVO.CodeName> roleList = entry.getValue();
            // 角色授权菜单树
            QueryVO<TenantRoleMenu> queryRoleMenu = new QueryVO<>(new TenantRoleMenu());
            queryRoleMenu.getData().setTenantId(tenantId);

            List<TenantRoleMenu> authMenuList = new ArrayList<>();
            Map<String, TenantRoleMenu> menuRoleMap = new HashMap<>();
            for (PowerVO.CodeName codeName : roleList) {
                queryRoleMenu.getData().setRoleCode(codeName.getCode());
                queryRoleMenu.getData().setRoleSource(codeName.getSource());
                // todo 查询角色菜单树
                authMenuList = roleService.getRoleMenuList(queryRoleMenu);
                menuRoleMap.clear();
                authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));
                // 角色对应产品菜单树
                TenantRole role = roleMap.get(codeName.getCode());
                menuList.forEach(item -> {
                    if (menuRoleMap.containsKey(item.getMenuCode())) {
                        if (!menuMap.containsKey(item.getMenuCode())) {
                            menuMap.put(item.getMenuCode(), item);
                        }
                        if (item.getMenuType() > 1) {
                            if (menuMap.get(item.getMenuCode()).getDataPower() == null || menuMap.get(item.getMenuCode()).getDataPower() > role.getDataPower()) {
                                menuMap.get(item.getMenuCode()).setDataPower(role.getDataPower());
                            }
                            Set<PowerVO.CodeName> orgList = powerVO.getRoleOrgMap().get(role.getRoleCode());
                            for (PowerVO.CodeName org : orgList) {
                                menuMap.get(item.getMenuCode()).getOrgCodeList().add(org.getCode());
                            }
                        }
                    }
                });
            }
        }
        powerVO.setMenuTree(new ArrayList<>(menuMap.values()));
    }

    /**
     * 生成菜单树
     * showAllOrg=false、showAllRole=true
     */
    private void getFTPowerMenuTree(Long tenantId, PowerVO powerVO, Map<String, TenantRole> roleMap, List<MenuVO> menuList) {
        Set<PowerVO.CodeName> roleList = powerVO.getOrgRoleMap().get(powerVO.getDefaultOrgCode());

        // 角色授权菜单树
        QueryVO<TenantRoleMenu> queryRoleMenu = new QueryVO<>(new TenantRoleMenu());
        queryRoleMenu.getData().setTenantId(tenantId);
        List<TenantRoleMenu> authMenuList = new ArrayList<>();
        Map<String, TenantRoleMenu> menuRoleMap = new HashMap<>();

        Map<String, MenuVO> menuMap = new HashMap<>();
        for (PowerVO.CodeName codeName : roleList) {
            queryRoleMenu.getData().setRoleCode(codeName.getCode());
            authMenuList = roleService.getRoleMenuList(queryRoleMenu);
            menuRoleMap.clear();
            authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));

            TenantRole role = roleMap.get(codeName.getCode());
            menuList.forEach(item -> {
                if (menuRoleMap.containsKey(item.getMenuCode())) {
                    if (!menuMap.containsKey(item.getMenuCode())) {
                        menuMap.put(item.getMenuCode(), item);
                    }
                    if (item.getMenuType() > 1) {
                        if (menuMap.get(item.getMenuCode()).getDataPower() == null || menuMap.get(item.getMenuCode()).getDataPower() > role.getDataPower()) {
                            menuMap.get(item.getMenuCode()).setDataPower(role.getDataPower());
                        }
                        menuMap.get(item.getMenuCode()).getOrgCodeList().add(powerVO.getDefaultOrgCode());
                    }
                }
            });
        }
        powerVO.setMenuTree(new ArrayList<>(menuMap.values()));
    }

    /**
     * 生成菜单树
     * showAllOrg=true、showAllRole=false
     */
    private void getTFPowerMenuTree(Long tenantId, PowerVO powerVO, Map<String, TenantRole> roleMap, List<MenuVO> menuList) {
        Map<String, MenuVO> menuMap = new HashMap<>();

        // 角色授权菜单树
        QueryVO<TenantRoleMenu> queryRoleMenu = new QueryVO<>(new TenantRoleMenu());
        queryRoleMenu.getData().setTenantId(tenantId);
        queryRoleMenu.getData().setRoleCode(powerVO.getDefaultRoleCode());
        List<TenantRoleMenu> authMenuList = roleService.getRoleMenuList(queryRoleMenu);

        Map<String, TenantRoleMenu> menuRoleMap = new HashMap<>();
        authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));

        TenantRole role = roleMap.get(powerVO.getDefaultRoleCode());
        menuList.forEach(item -> {
            if (menuRoleMap.containsKey(item.getMenuCode())) {
                if (!menuMap.containsKey(item.getMenuCode())) {
                    menuMap.put(item.getMenuCode(), item);
                }
                if (item.getMenuType() > 1) {
                    if (menuMap.get(item.getMenuCode()).getDataPower() == null || menuMap.get(item.getMenuCode()).getDataPower() > role.getDataPower()) {
                        menuMap.get(item.getMenuCode()).setDataPower(role.getDataPower());
                    }
                    Set<PowerVO.CodeName> orgList = powerVO.getRoleOrgMap().get(role.getRoleCode());
                    for (PowerVO.CodeName org : orgList) {
                        menuMap.get(item.getMenuCode()).getOrgCodeList().add(org.getCode());
                    }
                }
            }
        });
        powerVO.setMenuTree(new ArrayList<>(menuMap.values()));
    }

    /**
     * 生成菜单树
     * showAllOrg=false、showAllRole=false
     */
    private void getFFPowerMenuTree(Long tenantId, PowerVO powerVO, Map<String, TenantRole> roleMap, List<MenuVO> menuList) {
        Map<String, MenuVO> menuMap = new HashMap<>();

        Set<PowerVO.CodeName> orgList = powerVO.getRoleOrgMap().get(powerVO.getDefaultRoleCode());
        boolean flag = false;
        for (PowerVO.CodeName tmp : orgList) {
            if (!false && tmp.getCode().equals(powerVO.getDefaultOrgCode())) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new NCException("", "选定的机构不在选定角色能操作的机构列表中！");
        }

        // 角色授权菜单树
        QueryVO<TenantRoleMenu> queryRoleMenu = new QueryVO<>(new TenantRoleMenu());
        queryRoleMenu.getData().setTenantId(tenantId);
        queryRoleMenu.getData().setRoleCode(powerVO.getDefaultRoleCode());
        List<TenantRoleMenu> authMenuList = roleService.getRoleMenuList(queryRoleMenu);

        Map<String, TenantRoleMenu> menuRoleMap = new HashMap<>();
        authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));

        TenantRole role = roleMap.get(powerVO.getDefaultRoleCode());
        menuList.forEach(item -> {
            if (menuRoleMap.containsKey(item.getMenuCode())) {
                if (!menuMap.containsKey(item.getMenuCode())) {
                    menuMap.put(item.getMenuCode(), item);
                }
                if (item.getMenuType() > 1) {
                    if (menuMap.get(item.getMenuCode()).getDataPower() == null || menuMap.get(item.getMenuCode()).getDataPower() > role.getDataPower()) {
                        menuMap.get(item.getMenuCode()).setDataPower(role.getDataPower());
                    }
                    menuMap.get(item.getMenuCode()).getOrgCodeList().add(powerVO.getDefaultOrgCode());
                }
            }
        });
        powerVO.setMenuTree(new ArrayList<>(menuMap.values()));
    }

    private void handelDataPower(Long tenantId, List<MenuVO> menuList) {
        // 所有机构信息
        QueryVO<TenantOrg> queryAllOrg = new QueryVO<>(new TenantOrg());
        queryAllOrg.getData().setTenantId(tenantId);
        List<TenantOrg> allOrgList = orgMapper.selectListByMap(queryAllOrg.toMap());

        for (MenuVO menu : menuList) {
            if (menu.getMenuType() < 2) {
                continue;
            }
            if (menu.getDataPower() == 0) {
                // 0-所有数据
                allOrgList.forEach(item -> menu.getOrgCodeList().add(item.getOrgCode()));
                continue;
            }
            if (menu.getDataPower() == 1) {
                // 1-所属及下级机构数据
                menu.getOrgCodeList().addAll(getChildren(menu.getOrgCodeList(), allOrgList));
                continue;
            }
            if (menu.getDataPower() == 2) {
                // 2-所属机构数据
                continue;
            }
            if (menu.getDataPower() == 3) {
                // 3-用户自己的数据
                menu.setOrgCodeList(null);
            }
        }
    }

    private Set<String> getChildren(Set<String> orgList, List<TenantOrg> allOrgList) {
        Set<String> chiList = new HashSet<>();
        for (TenantOrg org : allOrgList) {
            if (orgList.contains(org.getParentCode())) {
                chiList.add(org.getOrgCode());
            }
        }
        if (!chiList.isEmpty()) {
            chiList.addAll(getChildren(chiList, allOrgList));
        }
        return chiList;
    }
}
