package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.cache.ConfigCache;
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
import com.nodecollege.cloud.dao.mapper.*;
import com.nodecollege.cloud.service.AdminService;
import com.nodecollege.cloud.service.PasswordPolicyService;
import com.nodecollege.cloud.service.ProductMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * 管理员service
 *
 * @author LC
 * @date 2019/11/27 19:36
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private OperateAdminMapper adminMapper;

    @Autowired
    private OperateAdminPasswordMapper operateAdminPasswordMapper;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private OperateAdminOrgRoleMapper adminRoleMapper;

    @Autowired
    private OperateAdminOrgRoleMapper adminOrgRoleMapper;

    @Autowired
    private OperateAdminOrgMapper adminOrgMapper;

    @Autowired
    private OperateOrgMapper orgMapper;

    @Autowired
    private OperateRoleMapper roleMapper;

    @Autowired
    private ProductMenuService productMenuService;

    @Autowired
    private OperateRoleMenuMapper roleMenuMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询管理员列表
     *
     * @param query
     */
    @Override
    public NCResult<OperateAdmin> getAdminList(QueryVO<OperateAdmin> query) {
        List<OperateAdmin> list = new ArrayList<>();
        Long total = 0L;
        if (NCConstants.INT_NEGATIVE_1.equals(query.getPageSize())) {
            list = adminMapper.selectAdminListByMap(query.toMap());
            total = NCUtils.isNullOrEmpty(list) ? 0 : Long.parseLong(list.size() + "");
        } else {
            Page page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
            if (query.isSort()) {
                page.setOrderBy(query.getSortKey() + " " + query.getSortDirection());
            }
            list = adminMapper.selectAdminListByMap(query.toMap());
            total = page.getTotal();
        }
        return NCResult.ok(list, total);
    }

    /**
     * 新增管理员
     *
     * @param loginVO
     */
    @Override
    public OperateAdmin addAdmin(LoginVO loginVO) {
        // 0 必填信息校验
        NCUtils.nullOrEmptyThrow(loginVO.getAccount());

        loginVO.setPassword(ConfigCache.DEFAULT_PWD);

        NCUtils.nullOrEmptyThrow(loginVO.getAccount(), new NCException("-1", "管理员密码不能为空！"));
        NCUtils.nullOrEmptyThrow(loginVO.getTelephone(), new NCException("-1", "管理员电话不能为空！"));
        if (!RegularExpUtils.checkTelephone(loginVO.getTelephone())) {
            throw new NCException("-1", "电话格式不正确！");
        }

        // 1 账号是否重复校验
        OperateAdmin admin = new OperateAdmin();
        admin.setAccount(loginVO.getAccount());
        List<OperateAdmin> result = adminMapper.selectAdminListByMap(new QueryVO<>(admin).toMap());
        if (!result.isEmpty()) {
            throw new NCException("-1", "当前账号已注册！");
        }

        // 2 手机号是否重复校验
        admin.setAccount(null);
        admin.setTelephone(loginVO.getTelephone());
        result = adminMapper.selectAdminListByMap(new QueryVO<>(admin).toMap());
        if (!result.isEmpty()) {
            throw new NCException("-1", "当前手机号已注册！");
        }
        // 3 保存数据
        admin.setAccount(loginVO.getAccount());
        admin.setCreateTime(new Date());
        admin.setState(1);
        // 管理员添加用户 首次登陆设为true
        admin.setFirstLogin(NCConstants.INT_1.equals(loginVO.getSource()));
        adminMapper.insertSelective(admin);

        // 查出新增管理员id
        List<OperateAdmin> result2 = adminMapper.selectAdminListByMap(new QueryVO<>(admin).toMap());

        String salt = PasswordUtil.getRandomSalt();
        String password = PasswordUtil.md5(loginVO.getPassword(), salt);

        OperateAdminPassword operateAdminPassword = new OperateAdminPassword();
        operateAdminPassword.setAdminId(result2.get(0).getAdminId());
        operateAdminPassword.setPassword(password);
        operateAdminPassword.setSalt(salt);
        operateAdminPassword.setCreateTime(new Date());
        operateAdminPasswordMapper.insertSelective(operateAdminPassword);

        // 清除登录失败次数缓存
        passwordPolicyService.deleteLoginFileNum(admin.getAccount());
        return admin;
    }

    /**
     * 修改管理员
     *
     * @param operateAdmin
     */
    @Override
    public void updateAdmin(OperateAdmin operateAdmin) {
        OperateAdmin exit = adminMapper.selectByPrimaryKey(operateAdmin.getAdminId());

        NCUtils.nullOrEmptyThrow(exit, new NCException("-1", "修改管理员失败，无此用户！"));
        if (!RegularExpUtils.checkTelephone(operateAdmin.getTelephone())) {
            throw new NCException("-1", "电话格式不正确！");
        }
        OperateAdmin query = new OperateAdmin();
        query.setTelephone(operateAdmin.getTelephone());
        List<OperateAdmin> result = adminMapper.selectAdminListByMap(new QueryVO<>(query).toMap());
        if (result.size() == 1 && !exit.getAdminId().equals(result.get(0).getAdminId())) {
            throw new NCException("", "该电话号码已存在！");
        }

        // 不能修改账号
        operateAdmin.setAccount(null);
        // 不能修改状态
        operateAdmin.setState(null);
        // 不能修改创建时间
        operateAdmin.setCreateTime(null);
        adminMapper.updateByPrimaryKeySelective(operateAdmin);
    }

    /**
     * 更新管理员密码
     *
     * @param account     管理员账号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param cert        图片验证码
     */
    @Override
    public void updateAdminPassword(String account, String oldPassword, String newPassword, String cert) {
        // 登陆前校验
        passwordPolicyService.checkLoginPolicy(account, oldPassword, newPassword, cert);

        OperateAdmin query = new OperateAdmin();
        query.setAccount(account);
        List<OperateAdmin> adminList = adminMapper.selectAdminListByMap(new QueryVO<>(query).toMap());

        NCUtils.nullOrEmptyThrow(adminList, new NCException("-1", "更新管理员密码失败！不存在该账号信息！"));

        // 根据管理员id获取密码信息
        List<OperateAdminPassword> list = operateAdminPasswordMapper.selectListByAdminId(adminList.get(0).getAdminId());

        // 旧密码校验
        if (!list.get(0).getPassword().equals(PasswordUtil.md5(oldPassword, list.get(0).getSalt()))) {
            Integer loginFileNum = passwordPolicyService.setLoginFileNum(account);
            throw new NCException("-1", MessageFormat.format("旧密码输入错误，请重新输入！(第{0}次)", loginFileNum));
        }

        // 注册密码校验
        passwordPolicyService.checkRegisterPolicy(newPassword, account);

        // 更新密码校验
        passwordPolicyService.checkUpdatePolicy(list, newPassword);

        // 更新密码
        OperateAdminPassword password = new OperateAdminPassword();
        password.setAdminId(adminList.get(0).getAdminId());
        password.setSalt(PasswordUtil.getRandomSalt());
        password.setPassword(PasswordUtil.md5(newPassword, password.getSalt()));
        password.setCreateTime(new Date());
        operateAdminPasswordMapper.insertSelective(password);

        // 管理员信息设置成非首次登录
        OperateAdmin updateAdmin = new OperateAdmin();
        updateAdmin.setAdminId(adminList.get(0).getAdminId());
        updateAdmin.setFirstLogin(false);
        adminMapper.updateByPrimaryKeySelective(updateAdmin);

        // 清除登录失败次数缓存
        passwordPolicyService.deleteLoginFileNum(account);
    }

    /**
     * 根据账号获取管理员信息
     *
     * @param account
     */
    @Override
    public OperateAdmin getAdminByAccount(String account) {
        OperateAdmin admin = new OperateAdmin();
        admin.setAccount(account);
        List<OperateAdmin> result = adminMapper.selectAdminListByMap(new QueryVO<>(admin).toMap());

        NCUtils.nullOrEmptyThrow(result, new NCException("-1", "根据账号未获取到用户信息！"));

        return result.get(0);
    }

    /**
     * 删除管理员
     *
     * @param id
     */
    @Override
    public void delAdmin(Long id) {
        // 根据id查询管理员信息
        OperateAdmin admin = adminMapper.selectByPrimaryKey(id);

        NCUtils.nullOrEmptyThrow(admin, new NCException("-1", "该管理员不存在！"));

        if (0 == admin.getState()) {
            throw new NCException("-1", "该管理员不能删除");
        }
        admin.setState(NCConstants.INT_NEGATIVE_1);
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    /**
     * 冻结/解冻管理员
     *
     * @param operateAdmin
     */
    @Override
    public void lockAdmin(OperateAdmin operateAdmin) {
        // 根据id查询管理员信息
        OperateAdmin admin = adminMapper.selectByPrimaryKey(operateAdmin.getAdminId());
        NCUtils.nullOrEmptyThrow(admin, new NCException("-1", "该管理员不存在！"));
        if (0 == admin.getState()) {
            throw new NCException("-1", "超级管理员不能冻结！");
        }
        if (!Arrays.asList(1, 2).contains(operateAdmin.getState())) {
            throw new NCException("", "只能进行冻结或者解冻操作");
        }
        OperateAdmin lockAdmin = new OperateAdmin();
        lockAdmin.setAdminId(operateAdmin.getAdminId());
        lockAdmin.setState(operateAdmin.getState());
        adminMapper.updateByPrimaryKeySelective(lockAdmin);
    }

    /**
     * 重置管理员密码
     *
     * @param admin
     */
    @Override
    public void resetPwd(OperateAdmin admin) {
        // 根据id查询管理员信息
        OperateAdmin ex = adminMapper.selectByPrimaryKey(admin.getAdminId());
        NCUtils.nullOrEmptyThrow(ex, new NCException("-1", "该管理员不存在！"));
        if (0 == ex.getState()) {
            throw new NCException("-1", "超级管理员不能重置密码！");
        }
        // 更新密码
        OperateAdminPassword password = new OperateAdminPassword();
        password.setAdminId(ex.getAdminId());
        password.setSalt(PasswordUtil.getRandomSalt());
        password.setPassword(PasswordUtil.md5(ConfigCache.DEFAULT_PWD, password.getSalt()));
        password.setCreateTime(new Date());
        operateAdminPasswordMapper.insertSelective(password);

        // 管理员信息设置成非首次登录
        OperateAdmin updateAdmin = new OperateAdmin();
        updateAdmin.setAdminId(admin.getAdminId());
        updateAdmin.setFirstLogin(true);
        adminMapper.updateByPrimaryKeySelective(updateAdmin);
    }

    @Override
    public void bindAdminOrg(BindVO bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getSourceIds());
        // 已绑定的数据
        OperateAdminOrg query = new OperateAdminOrg();
        query.setAdminId(bindVO.getSourceIds().get(0));
        List<OperateAdminOrg> exList = adminOrgMapper.selectList(query);

        List<String> exCodeList = new ArrayList<>();
        exList.forEach(item -> {
            if (NCUtils.isNullOrEmpty(bindVO.getTargetCodes()) || !bindVO.getTargetCodes().contains(item.getOrgCode())) {
                OperateAdminOrgRole queryRole = new OperateAdminOrgRole();
                queryRole.setAdminId(bindVO.getSourceIds().get(0));
                queryRole.setOrgCode(item.getOrgCode());
                List<OperateAdminOrgRole> exRole = adminOrgRoleMapper.selectList(queryRole);
                NCUtils.notNullOrNotEmptyThrow(exRole, "", item.getOrgCode() + "机构存在绑定管理员！不能解绑！");
                adminOrgMapper.deleteByPrimaryKey(item.getId());
            } else {
                exCodeList.add(item.getOrgCode());
            }
        });
        bindVO.getTargetCodes().forEach(item -> {
            if (!exCodeList.contains(item)) {
                OperateAdminOrg add = new OperateAdminOrg();
                add.setOrgCode(item);
                add.setAdminId(bindVO.getSourceIds().get(0));
                adminOrgMapper.insertSelective(add);
            }
        });
    }

    @Override
    public List<OperateAdmin> getAdminListByOrg(QueryVO<OperateAdminOrg> queryVO) {
        return adminMapper.selectListByOrg(queryVO.toMap());
    }

    @Override
    public List<OperateAdmin> getAdminListByRoleOrg(OperateAdminOrgRole queryVO) {
        return adminMapper.selectListByRoleOrg(queryVO);
    }

    @Override
    public void addAdminOrgRole(OperateAdminOrgRole bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getAdminId());
        NCUtils.nullOrEmptyThrow(bindVO.getOrgCode());
        NCUtils.nullOrEmptyThrow(bindVO.getRoleCode());
        List<OperateAdminOrgRole> exList = adminOrgRoleMapper.selectList(bindVO);
        NCUtils.notNullOrNotEmptyThrow(exList, "", "管理员机构角色已绑定！");
        adminOrgRoleMapper.insert(bindVO);
    }

    @Override
    public void delAdminOrgRole(OperateAdminOrgRole bindVO) {
        NCUtils.nullOrEmptyThrow(bindVO.getAdminId());
        NCUtils.nullOrEmptyThrow(bindVO.getOrgCode());
        NCUtils.nullOrEmptyThrow(bindVO.getRoleCode());
        List<OperateAdminOrgRole> exList = adminOrgRoleMapper.selectList(bindVO);
        NCUtils.nullOrEmptyThrow(exList, "", "管理员机构角色已解绑！");
        adminOrgRoleMapper.deleteByPrimaryKey(exList.get(0).getId());
    }

    @Override
    public PowerVO getAdminPower(OperateAdmin admin) {
        NCUtils.nullOrEmptyThrow(admin.getAdminId());
        NCUtils.nullOrEmptyThrow(admin.getShowAllOrg());
        NCUtils.nullOrEmptyThrow(admin.getShowAllRole());
        if (!admin.getShowAllRole() && StringUtils.isBlank(admin.getDefaultRoleCode())) {
            throw new NCException("", "默认角色必选");
        }
        if (!admin.getShowAllOrg() && StringUtils.isBlank(admin.getDefaultOrgCode())) {
            throw new NCException("", "默认机构必选");
        }

        OperateAdmin ex = adminMapper.selectByPrimaryKey(admin.getAdminId());
        NCUtils.nullOrEmptyThrow(ex, "", "管理员不存在！");

        // 所有角色
        Map<String, OperateOrg> orgMap = new HashMap<>();
        Map<String, OperateRole> roleMap = new HashMap<>();

        // 1 获取基础授权数据
        PowerVO powerVO = getBaseAdminPower(admin, orgMap, roleMap);

        // 2 获取菜单列表
        if (admin.getShowAllOrg()) {
            if (admin.getShowAllRole()) {
                // 获取 showAllOrg=true、showAllRole=true 菜单树
                getPowerMenuTree(0, powerVO, roleMap);
            } else {
                // 获取 showAllOrg=true、showAllRole=false 菜单树
                getTFPowerMenuTree(0, powerVO, roleMap);
            }
        } else {
            if (admin.getShowAllRole()) {
                // 获取 showAllOrg=false、showAllRole=true 菜单树
                getFTPowerMenuTree(0, powerVO, roleMap);
            } else {
                // 获取 showAllOrg=false、showAllRole=false 菜单树
                getFFPowerMenuTree(0, powerVO, roleMap);
            }
        }

        // 3 数据权限处理
        handelDataPower(0, powerVO.getMenuTree());
        powerVO.setMenuTree(MenuVO.getMenuTree(powerVO.getMenuTree()));
        return powerVO;
    }

    @Override
    public NCLoginUserVO changeDefaultOption(String accessToken, OperateAdmin admin) {

        OperateAdmin update = new OperateAdmin();
        update.setAdminId(admin.getAdminId());
        update.setDefaultOrgCode(admin.getDefaultOrgCode());
        update.setDefaultRoleCode(admin.getDefaultRoleCode());
        update.setShowAllOrg(admin.getShowAllOrg());
        update.setShowAllRole(admin.getShowAllRole());
        adminMapper.updateByPrimaryKeySelective(update);

        PowerVO powerVO = getAdminPower(admin);

        NCLoginUserVO adminLoginInfo = new NCLoginUserVO(powerVO);
        adminLoginInfo.setLoginId(admin.getAdminId());
        adminLoginInfo.setAccount(admin.getAccount());
        adminLoginInfo.setNickname(admin.getAccount());
        adminLoginInfo.setAccessToken(accessToken);
        adminLoginInfo.setUuid(NCUtils.getUUID());
        Long expire = redisUtils.getExpire(RedisConstants.ADMIN_LOGIN_INFO + accessToken);
        adminLoginInfo.setExpire(expire != null ? expire.intValue() : 0);

        redisUtils.set(RedisConstants.ADMIN_LOGIN_INFO + accessToken, adminLoginInfo, expire != null ? expire.intValue() : 0);
        return adminLoginInfo;
    }

    /***
     * 基础数据处理
     */
    private PowerVO getBaseAdminPower(OperateAdmin admin, Map<String, OperateOrg> orgMap, Map<String, OperateRole> roleMap) {
        PowerVO powerVO = new PowerVO();
        powerVO.setShowAllOrg(admin.getShowAllOrg());
        powerVO.setDefaultOrgCode(admin.getDefaultOrgCode());
        powerVO.setShowAllRole(admin.getShowAllRole());
        powerVO.setDefaultRoleCode(admin.getDefaultRoleCode());
        powerVO.setOrgList(new HashSet<>());
        powerVO.setRoleList(new HashSet<>());
        powerVO.setOrgRoleMap(new HashMap<>());
        powerVO.setRoleOrgMap(new HashMap<>());

        // 查询所有授权机构
        QueryVO<OperateAdminOrg> queryOrg = new QueryVO<>(new OperateAdminOrg());
        queryOrg.getData().setAdminId(admin.getAdminId());
        List<OperateOrg> orgList = orgMapper.selectListByAdmin(queryOrg.toMap());


        QueryVO<OperateRoleOrg> queryRoleOrg = new QueryVO<>(new OperateRoleOrg());
        queryRoleOrg.getData().setRoleOrgUsage(0);
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
                    OperateAdminOrgRole queryAOR = new OperateAdminOrgRole();
                    queryAOR.setAdminId(admin.getAdminId());
                    queryAOR.setRoleCode(role.getRoleCode());
                    queryAOR.setOrgCode(org.getOrgCode());
                    List<OperateAdmin> exAdminList = adminMapper.selectListByRoleOrg(queryAOR);
                    if (!exAdminList.isEmpty()) {
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

























