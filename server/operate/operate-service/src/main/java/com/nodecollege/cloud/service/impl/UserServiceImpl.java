package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.constants.NCConstants;
import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.*;
import com.nodecollege.cloud.common.model.po.*;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.PasswordUtil;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.common.utils.RegularExpUtils;
import com.nodecollege.cloud.cache.ConfigCache;
import com.nodecollege.cloud.dao.mapper.*;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.FileService;
import com.nodecollege.cloud.service.PasswordPolicyService;
import com.nodecollege.cloud.service.ProductMenuService;
import com.nodecollege.cloud.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * userService
 *
 * @author LC
 * @date 2019/6/13 15:49
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private OperateUserMapper userMapper;

    @Autowired
    private OperateOrgMapper orgMapper;

    @Autowired
    private OperateRoleMapper roleMapper;

    @Autowired
    private ProductMenuService productMenuService;

    @Autowired
    private OperateRoleMenuMapper roleMenuMapper;

    @Autowired
    private OperateUserOrgMapper userOrgMapper;

    @Autowired
    private OperateUserOrgRoleMapper userOrgRoleMapper;

    @Autowired
    private OperateUserPasswordMapper operateUserPasswordMapper;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private OperateTenantMapper operateTenantMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private OperateUserInviteMapper invitationMapper;

    @Autowired
    private OperateUserTenantMapper userTenantMapper;

    @Autowired
    private TenantClient tenantClient;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询所有用户信息
     */
    @Override
    public NCResult<OperateUser> list(QueryVO<OperateUser> query) {
        List<OperateUser> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(query.getPageSize())) {
            list = userMapper.selectUserListByMap(query.toMap());
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
            if (query.isSort()) {
                page.setOrderBy(query.getSortKey() + " " + query.getSortDirection());
            }
            list = userMapper.selectUserListByMap(query.toMap());
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    /**
     * 根据用户账号获取用户信息
     *
     * @param account
     */
    @Override
    public OperateUser getUserByAccount(String account) {
        OperateUser user = new OperateUser();
        user.setAccount(account);
        List<OperateUser> result = userMapper.selectUserListByMap(new QueryVO<>(user).toMap());
        NCUtils.nullOrEmptyThrow(result, "-1", "根据账号未获取到用户信息");
        return result.get(0);
    }

    /**
     * 用户注册
     *
     * @param loginVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(LoginVO loginVO) {
        NCUtils.nullOrEmptyThrow(loginVO.getPassword());
        NCUtils.nullOrEmptyThrow(loginVO.getTelephone());
        // 1 校验账号是否已经注册
        QueryVO<OperateUser> query = new QueryVO<>(new OperateUser());
        query.getData().setTelephone(loginVO.getTelephone());
        List<OperateUser> result = userMapper.selectUserListByMap(query.toMap());
        NCUtils.notNullOrNotEmptyThrow(result, "-1", "此电话已注册");

        // 校验密码规则
        passwordPolicyService.checkRegisterPolicy(loginVO.getPassword(), loginVO.getAccount());

        // 查询邀请信息
        QueryVO<OperateUserInvite> queryInvite = new QueryVO<>(new OperateUserInvite());
        queryInvite.getData().setTelephone(loginVO.getTelephone());
        queryInvite.getData().setState(0);
        List<OperateUserInvite> exList = invitationMapper.selectListByMap(query.toMap());

        // 2 保存用户信息
        OperateUser addUser = new OperateUser();
        addUser.setAccount(loginVO.getTelephone());
        addUser.setTelephone(loginVO.getTelephone());
        addUser.setNickname(loginVO.getTelephone());
        addUser.setCreateTime(new Date());
        addUser.setState(1);
        addUser.setSalt(PasswordUtil.getRandomSalt());
        addUser.setPassword(PasswordUtil.md5(loginVO.getPassword(), addUser.getSalt()));
        addUser.setFirstLogin(1);
        addUser.setShowAllOrg(true);
        addUser.setShowAllRole(true);
        if (!exList.isEmpty()) {
            OperateTenant tenant = operateTenantMapper.selectByPrimaryKey(exList.get(0).getTenantId());
            addUser.setTenantId(exList.get(0).getTenantId());
            addUser.setTenantCode(tenant.getTenantCode());
        }
        userMapper.insertSelective(addUser);

        // 自动授权默认机构
        if (ConfigCache.DEFAULT_USER_ORG_LIST != null) {
            for (int i = 0; i < ConfigCache.DEFAULT_USER_ORG_LIST.size(); i++) {
                OperateUserOrg add = new OperateUserOrg();
                add.setOrgCode(ConfigCache.DEFAULT_USER_ORG_LIST.get(i));
                add.setUserId(addUser.getUserId());
                userOrgMapper.insertSelective(add);
            }
        }

        for (OperateUserInvite invite : exList) {
            // 添加用户租户关系
            OperateTenant tenant = operateTenantMapper.selectByPrimaryKey(invite.getTenantId());
            OperateUserTenant userTenant = new OperateUserTenant();
            userTenant.setTenantId(tenant.getTenantId());
            userTenant.setUserId(addUser.getUserId());
            userTenant.setState(1);
            userTenant.setCreateTime(new Date());
            userTenant.setCreateUser(tenant.getCreateUser());
            userTenantMapper.insertSelective(userTenant);

            invite.setState(1);
            invite.setUpdateTime(new Date());
            invitationMapper.updateByPrimaryKeySelective(invite);

            // todo 通知租户添加成员
            addUser.setTenantId(invite.getTenantId());
            addUser.setNickname(invite.getUserName());
            tenantClient.inviteMemberSuccess(addUser);
        }
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NCLoginUserVO updateUser(OperateUser user, String userAccessToken) {
        NCUtils.nullOrEmptyThrow(user.getUserId(), "-1", "更新用户信息失败，用户主键不能为空！");
        NCUtils.nullOrEmptyThrow(user.getAccount(), "-1", "更新用户信息失败，用户账号不能为空！");
        NCUtils.nullOrEmptyThrow(user.getNickname(), "-1", "更新用户信息失败，用户名称不能为空！");
        NCUtils.nullOrEmptyThrow(user.getTelephone(), "-1", "更新用户信息失败，用户电话不能为空！");
        OperateUser oldUser = getUserByUserId(user.getUserId());
        NCUtils.nullOrEmptyThrow(oldUser, "", "该用户不存在！");

        if (!RegularExpUtils.checkTelephone(user.getTelephone())) {
            throw new NCException("-1", "电话格式不正确！");
        }

        OperateUser queryUser = new OperateUser();
        queryUser.setAccount(user.getAccount());
        List<OperateUser> exList = userMapper.selectUserListByMap(new QueryVO<>(queryUser).toMap());
        if (exList.size() == 1 && !exList.get(0).getUserId().equals(user.getUserId())) {
            throw new NCException("-1", "该账号已存在！");
        }

        queryUser = new OperateUser();
        queryUser.setTelephone(user.getTelephone());
        exList = userMapper.selectUserListByMap(new QueryVO<>(queryUser).toMap());
        if (exList.size() == 1 && !exList.get(0).getUserId().equals(user.getUserId())) {
            throw new NCException("-1", "该手机号已存在！");
        }

        OperateUser updateUser = new OperateUser();
        updateUser.setUserId(user.getUserId());
        updateUser.setNickname(user.getNickname());
        updateUser.setAccount(user.getAccount());
        updateUser.setTelephone(user.getTelephone());
        updateUser.setEmail(user.getEmail());
        updateUser.setSex(user.getSex());
        updateUser.setBirthday(user.getBirthday());
        userMapper.updateByPrimaryKeySelective(updateUser);
        if (StringUtils.isNotBlank(userAccessToken)) {
            NCLoginUserVO loginUser = redisUtils.get(RedisConstants.USER_LOGIN_INFO + userAccessToken, NCLoginUserVO.class);
            Long expire = redisUtils.getExpire(RedisConstants.USER_LOGIN_INFO + userAccessToken);
            if (loginUser != null && expire != null) {
                loginUser = new NCLoginUserVO(getUserPower(user));
                loginUser.setLoginId(user.getUserId());
                loginUser.setAccount(user.getAccount());
                loginUser.setNickname(StringUtils.isBlank(user.getNickname()) ? user.getAccount() : user.getNickname());
                loginUser.setAvatar(user.getAvatar());
                loginUser.setAccessToken(userAccessToken);
                loginUser.setUuid(NCUtils.getUUID());
                loginUser.setExpire(expire.intValue());
                loginUser.setTenantId(user.getTenantId());
                loginUser.setAvatar(user.getAvatar());
                loginUser.setAvatarThumb(user.getAvatarThumb());

                // 初始化用户信息数据权限信息
                redisUtils.set(RedisConstants.USER_LOGIN_INFO + userAccessToken, loginUser, expire);

                // 清除登录失败次数缓存
                passwordPolicyService.deleteUserLoginFileNum(user.getAccount());
                clearUserCache(user.getUserId());
                return loginUser;
            }
        }
        clearUserCache(user.getUserId());
        return null;
    }

    /**
     * 删除用户信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delUser(Long id) {
        OperateUser user = getUserByUserId(id);
        NCUtils.nullOrEmptyThrow(user, "-1", "用户删除失败，根据id未查询到用户信息！");

        // todo 查询用户租户关系
        OperateUserTenant queryTenant = new OperateUserTenant();
        queryTenant.setUserId(id);
        queryTenant.setState(1);
        List<OperateUserTenant> exTenant = userTenantMapper.selectList(queryTenant);
        NCUtils.notNullOrNotEmptyThrow(exTenant, "", "存在绑定租户不能删除！");

        user.setState(NCConstants.INT_NEGATIVE_1);
        userMapper.updateByPrimaryKeySelective(user);
        clearUserCache(id);
    }

    /**
     * 重置密码, 运维人员使用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(OperateUser operateUser) {
        NCUtils.nullOrEmptyThrow(operateUser.getUserId(), "", "用户id不能为空！");
        // 1 根据用户id获取用户信息
        OperateUser user = getUserByUserId(operateUser.getUserId());
        NCUtils.nullOrEmptyThrow(user, "-1", "重置密码失败，根据id获取用户信息失败！");
        // 2 获取默认密码配置数据
//        List<Config> config = configDao.getConfigsByCodeAndTenantId(ConfigData.DEFAULT_PASSWORD, query.getTenantId());
//        if (config.isEmpty()) {
//            throw "-1", "重置密码失败，获取默认密码失败，请联系管理员处理！");
//        }
        // 3 旧密码存入密码表中
        OperateUserPassword userPassword = new OperateUserPassword();
        userPassword.setUserId(operateUser.getUserId());
        userPassword.setSalt(user.getSalt());
        userPassword.setPassword(user.getPassword());
        userPassword.setCreateTime(new Date());
        operateUserPasswordMapper.insertSelective(userPassword);

        // 4 将新密码进行保存更新
        user.setSalt(PasswordUtil.getRandomSalt());
        user.setPassword(PasswordUtil.md5(ConfigCache.DEFAULT_PWD, user.getSalt()));
        user.setFirstLogin(0);
        userMapper.updateByPrimaryKeySelective(user);
        clearUserCache(operateUser.getUserId());
    }

    /**
     * 修改密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePwd(Long userId, String oldPassword, String newPassword, ApiDataPower apiDataPower) {
        OperateUser user = getUserByUserId(userId);
        NCUtils.nullOrEmptyThrow(user, "-1", "修改密码失败，根据用户id未获取到用户信息！");
        oldPassword = PasswordUtil.md5(oldPassword, user.getSalt());
        if (!oldPassword.equals(user.getPassword())) {
            throw new NCException("-1", "修改密码失败，旧密码输入错误！");
        }

        // 校验新密码是否满足规则
        passwordPolicyService.checkRegisterPolicy(newPassword, user.getAccount());

        // 登陆成功后校验密码是否和历史密码一致
        passwordPolicyService.checkUserLoginSuccessPolicy(userId, 1);

        // 将旧密码存于密码表中
        OperateUserPassword userPassword = new OperateUserPassword();
        userPassword.setSalt(user.getSalt());
        userPassword.setPassword(user.getPassword());
        userPassword.setCreateTime(new Date());
        operateUserPasswordMapper.insertSelective(userPassword);

        // 更新新密码
        user.setSalt(PasswordUtil.getRandomSalt());
        user.setPassword(PasswordUtil.md5(newPassword, user.getSalt()));
        user.setFirstLogin(1);
        userMapper.updateByPrimaryKeySelective(user);

        // 清除登陆失败次数
        passwordPolicyService.deleteUserLoginFileNum(user.getAccount());
        clearUserCache(userId);
    }

    /**
     * 切换默认租户
     *
     * @param loginUser
     */
    @Override
    public void changeDefaultTenant(OperateUserTenant loginUser) {
        NCUtils.nullOrEmptyThrow(loginUser.getTenantId(), "", "租户id不能为空！");

        OperateTenant exTenant = operateTenantMapper.selectByPrimaryKey(loginUser.getTenantId());
        NCUtils.nullOrEmptyThrow(exTenant, "", "该租户不存在！");

        OperateUser user = getUserByUserId(loginUser.getUserId());
        NCUtils.nullOrEmptyThrow(user, "", "该用户不存在！");

        // 校验租户成员信息
        OperateUserTenant queryUserTenant = new OperateUserTenant();
        queryUserTenant.setTenantId(loginUser.getTenantId());
        queryUserTenant.setUserId(loginUser.getUserId());
        List<OperateUserTenant> exUserTenant = userTenantMapper.selectList(queryUserTenant);
        NCUtils.nullOrEmptyThrow(exUserTenant, "", "不是该企业成员！");

        OperateUser updateUser = new OperateUser();
        updateUser.setUserId(user.getUserId());
        updateUser.setTenantId(loginUser.getTenantId());
        updateUser.setTenantCode(exTenant.getTenantCode());
        userMapper.updateByPrimaryKeySelective(updateUser);

        clearUserCache(loginUser.getUserId());
    }

    /**
     * 根据昵称查询用户信息
     *
     * @param queryVO
     * @return
     */
    @Override
    public NCResult<OperateUser> getUserListByNickname(QueryVO<OperateUser> queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getData().getNickname(), "", "请输入昵称");
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateUser> list = userMapper.getUserListByNickname(queryVO.toMap());
        Long total = page.getTotal();
        return NCResult.ok(list, total);
    }

    /**
     * 根据用户id查询用户信息
     *
     * @param queryVO
     * @return
     */
    @Override
    public List<OperateUser> getUserListByQuery(QueryVO queryVO) {
        NCUtils.nullOrEmptyThrow(queryVO.getLongList(), "", "主键列表不能为空！");
        List<OperateUser> list = new ArrayList<>(queryVO.getLongList().size());
        for (int i = 0; i < queryVO.getLongList().size(); i++) {
            OperateUser user = getUserByUserId(queryVO.getLongList().get(i));
            if (user != null) {
                list.add(user);
            }
        }
        return list;
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public OperateUser getUserByUserId(Long userId) {
        String userKey = RedisConstants.USER_ID_INFO + userId;
        OperateUser user = redisUtils.get(userKey, OperateUser.class);
        if (user == null) {
            user = userMapper.selectByPrimaryKey(userId);
            if (user != null && StringUtils.isNotBlank(user.getAvatar())) {
                user.setAvatar(user.getAvatar());
                int temp = user.getAvatar().indexOf(".");
                user.setAvatarThumb(user.getAvatar().substring(0, temp) + "_80x80" + user.getAvatar().substring(temp));
            }
            int exp = new Random().nextInt(60 * 10);
            redisUtils.set(userKey, user, 60 * 60 * 2 + exp);
        }
        return user;
    }

    /**
     * 清除用户缓存
     */
    @Override
    public void clearUserCache(Long userId) {
        redisUtils.delete(RedisConstants.USER_ID_INFO + userId);
    }

    /**
     * 上传头像
     *
     * @param avatarFile
     * @param userId
     * @return
     */
    @Override
    public OperateUser uploadAvatar(MultipartFile avatarFile, Long userId) {
        NCUtils.nullOrEmptyThrow(avatarFile, "", "头像文件不能为空！");
        NCUtils.nullOrEmptyThrow(userId, "", "用户id不能为空！");
        OperateUser user = getUserByUserId(userId);
        NCUtils.nullOrEmptyThrow(user, "-1", "该用户不存在！");

        OperateFile operateFile = new OperateFile();
        operateFile.setUserId(userId);
        operateFile.setFileType(1);
        operateFile.setFileName(avatarFile.getOriginalFilename());
        operateFile.setFilePurpose(0);
        operateFile = fileService.uploadImgThumb(avatarFile, operateFile);

        OperateUser update = new OperateUser();
        update.setUserId(userId);
        update.setAvatar(operateFile.getFilePath());
        userMapper.updateByPrimaryKeySelective(update);
        user.setAvatar(operateFile.getFilePath());
        clearUserCache(userId);
        return getUserByUserId(userId);
    }

    @Override
    public void bindUserOrg(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceIds());
        // 已绑定的数据
        OperateUserOrg query = new OperateUserOrg();
        query.setUserId(bindVO.getSourceIds().get(0));
        List<OperateUserOrg> exList = userOrgMapper.selectList(query);

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getOrgCode())) {
                OperateUserOrgRole queryRole = new OperateUserOrgRole();
                queryRole.setUserId(bindVO.getSourceIds().get(0));
                queryRole.setOrgCode(item.getOrgCode());
                List<OperateUserOrgRole> exRole = userOrgRoleMapper.selectList(queryRole);
                NCUtils.notNullOrNotEmptyThrow(exRole, "", item.getOrgCode() + "机构存在绑定用户！不能解绑！");
                userOrgMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getOrgCode());
            }
        });
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                OperateUserOrg add = new OperateUserOrg();
                add.setOrgCode(item);
                add.setUserId(bindVO.getSourceIds().get(0));
                userOrgMapper.insertSelective(add);
            }
        });
    }

    @Override
    public List<OperateUser> getUserListByOrg(QueryVO<OperateUserOrg> queryVO) {
        return userMapper.selectListByOrg(queryVO.toMap());
    }

    @Override
    public List<OperateUser> getUserListByRoleOrg(OperateUserOrgRole queryVO) {
        return userMapper.selectListByRoleOrg(queryVO);
    }

    @Override
    public void addUserOrgRole(OperateUserOrgRole bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getUserId());
        NCUtils.nullOrEmptyThrow(bindVO.getOrgCode());
        NCUtils.nullOrEmptyThrow(bindVO.getRoleCode());
        List<OperateUserOrgRole> exList = userOrgRoleMapper.selectList(bindVO);
        NCUtils.notNullOrNotEmptyThrow(exList, "", "用户机构角色已绑定！");
        userOrgRoleMapper.insert(bindVO);
    }

    @Override
    public void delUserOrgRole(OperateUserOrgRole bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getUserId());
        NCUtils.nullOrEmptyThrow(bindVO.getOrgCode());
        NCUtils.nullOrEmptyThrow(bindVO.getRoleCode());
        List<OperateUserOrgRole> exList = userOrgRoleMapper.selectList(bindVO);
        NCUtils.nullOrEmptyThrow(exList, "", "用户机构角色已解绑！");
        userOrgRoleMapper.deleteByPrimaryKey(exList.get(0).getId());
    }

    @Override
    public PowerVO getUserPower(OperateUser user) {
        NCUtils.nullOrEmptyThrow(user.getUserId());
        NCUtils.nullOrEmptyThrow(user.getShowAllOrg());
        NCUtils.nullOrEmptyThrow(user.getShowAllRole());
        if (!user.getShowAllRole() && StringUtils.isBlank(user.getDefaultRoleCode())) {
            throw new NCException("", "默认角色必选");
        }
        if (!user.getShowAllOrg() && StringUtils.isBlank(user.getDefaultOrgCode())) {
            throw new NCException("", "默认机构必选");
        }

        OperateUser ex = userMapper.selectByPrimaryKey(user.getUserId());
        NCUtils.nullOrEmptyThrow(ex, "", "管理员不存在！");

        // 所有角色
        Map<String, OperateOrg> orgMap = new HashMap<>();
        Map<String, OperateRole> roleMap = new HashMap<>();

        // 1 获取基础授权数据
        PowerVO powerVO = getBaseUserPower(user, orgMap, roleMap);

        // 2 获取菜单列表
        if (user.getShowAllOrg()) {
            if (user.getShowAllRole()) {
                // 获取 showAllOrg=true、showAllRole=true 菜单树
                getPowerMenuTree(1, powerVO, roleMap);
            } else {
                // 获取 showAllOrg=true、showAllRole=false 菜单树
                getTFPowerMenuTree(1, powerVO, roleMap);
            }
        } else {
            if (user.getShowAllRole()) {
                // 获取 showAllOrg=false、showAllRole=true 菜单树
                getFTPowerMenuTree(1, powerVO, roleMap);
            } else {
                // 获取 showAllOrg=false、showAllRole=false 菜单树
                getFFPowerMenuTree(1, powerVO, roleMap);
            }
        }

        // 3 数据权限处理
        handelDataPower(1, powerVO.getMenuTree());
        powerVO.setMenuTree(MenuVO.getMenuTree(powerVO.getMenuTree()));
        return powerVO;
    }

    @Override
    public NCLoginUserVO changeDefaultOption(String accessToken, OperateUser user) {
        OperateUser update = new OperateUser();
        update.setUserId(user.getUserId());
        update.setDefaultOrgCode(user.getDefaultOrgCode());
        update.setDefaultRoleCode(user.getDefaultRoleCode());
        update.setShowAllOrg(user.getShowAllOrg());
        update.setShowAllRole(user.getShowAllRole());
        userMapper.updateByPrimaryKeySelective(update);

        PowerVO powerVO = getUserPower(user);

        NCLoginUserVO userLoginInfo = new NCLoginUserVO(powerVO);
        userLoginInfo.setLoginId(user.getUserId());
        userLoginInfo.setAccount(user.getAccount());
        userLoginInfo.setNickname(user.getAccount());
        userLoginInfo.setAccessToken(accessToken);
        userLoginInfo.setUuid(NCUtils.getUUID());
        Long expire = redisUtils.getExpire(RedisConstants.USER_LOGIN_INFO + accessToken);
        userLoginInfo.setExpire(expire != null ? expire.intValue() : 0);

        redisUtils.set(RedisConstants.USER_LOGIN_INFO + accessToken, userLoginInfo, expire != null ? expire.intValue() : 0);
        return userLoginInfo;
    }

    /***
     * 基础数据处理
     */
    private PowerVO getBaseUserPower(OperateUser user, Map<String, OperateOrg> orgMap, Map<String, OperateRole> roleMap) {
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
        QueryVO<OperateUserOrg> queryOrg = new QueryVO<>(new OperateUserOrg());
        queryOrg.getData().setUserId(user.getUserId());
        List<OperateOrg> orgList = orgMapper.selectListByUser(queryOrg.toMap());


        QueryVO<OperateRoleOrg> queryRoleOrg = new QueryVO<>(new OperateRoleOrg());
        queryRoleOrg.getData().setRoleOrgUsage(1);
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
            List<OperateRole> roleList = roleMapper.selectListByOrg(queryRoleOrg.toMap());
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
                    powerVO.getOrgRoleMap().get(org.getOrgCode()).add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName()));
                    powerVO.getRoleOrgMap().get(role.getRoleCode()).add(new PowerVO.CodeName(org.getOrgCode(), org.getOrgName()));
                } else {
                    // 组织成员角色
                    OperateUserOrgRole queryAOR = new OperateUserOrgRole();
                    queryAOR.setUserId(user.getUserId());
                    queryAOR.setRoleCode(role.getRoleCode());
                    queryAOR.setOrgCode(org.getOrgCode());
                    List<OperateUser> exUserList = userMapper.selectListByRoleOrg(queryAOR);
                    if (!exUserList.isEmpty()) {
                        powerVO.getRoleList().add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName()));
                        powerVO.getOrgRoleMap().get(org.getOrgCode()).add(new PowerVO.CodeName(role.getRoleCode(), role.getRoleName()));
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
    private void getPowerMenuTree(Integer usage, PowerVO powerVO, Map<String, OperateRole> roleMap) {
        Map<String, MenuVO> menuMap = new HashMap<>();
        for (Map.Entry<String, Set<PowerVO.CodeName>> entry : powerVO.getOrgRoleMap().entrySet()) {
            Set<PowerVO.CodeName> roleList = entry.getValue();
            // 角色授权菜单树
            QueryVO<OperateRoleMenu> queryRoleMenu = new QueryVO<>(new OperateRoleMenu());
            queryRoleMenu.getData().setRoleMenuUsage(usage);

            OperateProductMenu queryMenu = new OperateProductMenu();
            List<OperateRoleMenu> authMenuList = new ArrayList<>();
            Map<String, OperateRoleMenu> menuRoleMap = new HashMap<>();
            for (PowerVO.CodeName codeName : roleList) {
                queryRoleMenu.getData().setRoleCode(codeName.getCode());
                authMenuList = roleMenuMapper.selectListByMap(queryRoleMenu.toMap());
                menuRoleMap.clear();
                authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));
                // 角色对应产品菜单树
                OperateRole role = roleMap.get(codeName.getCode());

                queryMenu.setProductCode(role.getProductCode());
                List<MenuVO> tempList = productMenuService.getProductMenuList(queryMenu);
                tempList.forEach(item -> {
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
    private void getFTPowerMenuTree(Integer usage, PowerVO powerVO, Map<String, OperateRole> roleMap) {
        Set<PowerVO.CodeName> roleList = powerVO.getOrgRoleMap().get(powerVO.getDefaultOrgCode());

        // 角色授权菜单树
        QueryVO<OperateRoleMenu> queryRoleMenu = new QueryVO<>(new OperateRoleMenu());
        queryRoleMenu.getData().setRoleMenuUsage(usage);
        List<OperateRoleMenu> authMenuList = new ArrayList<>();
        Map<String, OperateRoleMenu> menuRoleMap = new HashMap<>();

        Map<String, MenuVO> menuMap = new HashMap<>();
        OperateProductMenu queryMenu = new OperateProductMenu();
        for (PowerVO.CodeName codeName : roleList) {
            queryRoleMenu.getData().setRoleCode(codeName.getCode());
            authMenuList = roleMenuMapper.selectListByMap(queryRoleMenu.toMap());
            menuRoleMap.clear();
            authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));

            OperateRole role = roleMap.get(codeName.getCode());
            queryMenu.setProductCode(role.getProductCode());
            List<MenuVO> tempList = productMenuService.getProductMenuList(queryMenu);
            tempList.forEach(item -> {
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
    private void getTFPowerMenuTree(Integer usage, PowerVO powerVO, Map<String, OperateRole> roleMap) {
        Map<String, MenuVO> menuMap = new HashMap<>();

        // 角色授权菜单树
        QueryVO<OperateRoleMenu> queryRoleMenu = new QueryVO<>(new OperateRoleMenu());
        queryRoleMenu.getData().setRoleMenuUsage(usage);
        queryRoleMenu.getData().setRoleCode(powerVO.getDefaultRoleCode());
        List<OperateRoleMenu> authMenuList = roleMenuMapper.selectListByMap(queryRoleMenu.toMap());

        Map<String, OperateRoleMenu> menuRoleMap = new HashMap<>();
        authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));

        OperateProductMenu queryMenu = new OperateProductMenu();
        OperateRole role = roleMap.get(powerVO.getDefaultRoleCode());
        queryMenu.setProductCode(role.getProductCode());
        List<MenuVO> tempList = productMenuService.getProductMenuList(queryMenu);
        tempList.forEach(item -> {
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
    private void getFFPowerMenuTree(Integer usage, PowerVO powerVO, Map<String, OperateRole> roleMap) {
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
        QueryVO<OperateRoleMenu> queryRoleMenu = new QueryVO<>(new OperateRoleMenu());
        queryRoleMenu.getData().setRoleMenuUsage(usage);
        queryRoleMenu.getData().setRoleCode(powerVO.getDefaultRoleCode());
        List<OperateRoleMenu> authMenuList = roleMenuMapper.selectListByMap(queryRoleMenu.toMap());

        Map<String, OperateRoleMenu> menuRoleMap = new HashMap<>();
        authMenuList.forEach(item -> menuRoleMap.put(item.getMenuCode(), item));

        OperateProductMenu queryMenu = new OperateProductMenu();
        OperateRole role = roleMap.get(powerVO.getDefaultRoleCode());
        queryMenu.setProductCode(role.getProductCode());
        List<MenuVO> tempList = productMenuService.getProductMenuList(queryMenu);
        tempList.forEach(item -> {
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

    private void handelDataPower(Integer usage, List<MenuVO> menuList) {
        // 所有机构信息
        QueryVO<OperateOrg> queryAllOrg = new QueryVO<>(new OperateOrg());
        queryAllOrg.getData().setOrgUsage(usage);
        List<OperateOrg> allOrgList = orgMapper.selectListByMap(queryAllOrg.toMap());

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

    private Set<String> getChildren(Set<String> orgList, List<OperateOrg> allOrgList) {
        Set<String> chiList = new HashSet<>();
        for (OperateOrg org : allOrgList) {
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
