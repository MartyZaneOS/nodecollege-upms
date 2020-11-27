package com.nodecollege.cloud.service.impl;

import com.nodecollege.cloud.common.constants.RedisConstants;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCLoginUserVO;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.PowerVO;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateTenant;
import com.nodecollege.cloud.common.model.po.TenantMember;
import com.nodecollege.cloud.common.model.vo.LoginVO;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.common.utils.PasswordUtil;
import com.nodecollege.cloud.common.utils.RedisUtils;
import com.nodecollege.cloud.feign.TenantClient;
import com.nodecollege.cloud.service.LoginService;
import com.nodecollege.cloud.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LC
 * @date 2020/10/21 20:04
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static Integer expire = 2 * 60 * 60;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TenantClient tenantClient;

    @Override
    public NCLoginUserVO login(LoginVO loginVO, String loginTenantCode) {
        // 1 校验企业信息
        OperateTenant queryTenant = new OperateTenant();
        queryTenant.setTenantCode(loginTenantCode);
        NCResult<OperateTenant> tenantResult = tenantClient.getTenantInfo(queryTenant);
        if (!tenantResult.getSuccess() || tenantResult.getRows() == null || tenantResult.getRows().isEmpty()) {
            throw new NCException("-1", "不存在租户代码为（" + loginTenantCode +"）的企业！");
        }

        // 2 验证密码是否正确
        TenantMember query = new TenantMember();
        query.setAccount(loginVO.getAccount());
        query.setTenantId(tenantResult.getRows().get(0).getTenantId());
        List<TenantMember> result = memberService.list(new QueryVO<>(query));
        if (result.isEmpty()) {
            query.setAccount(null);
            query.setTelephone(loginVO.getTelephone());
            result = memberService.list(new QueryVO<>(query));
            NCUtils.nullOrEmptyThrow(result, new NCException("-1", "根据账号或者电话未获取到成员信息"));
        }
        TenantMember member = result.get(0);
        if (!member.getPassword().equals(PasswordUtil.md5(loginVO.getPassword(), member.getSalt()))) {
            throw new NCException("-1", "密码错误！请重新输入！");
        }

        return getLoginMember(member, loginTenantCode, tenantResult.getRows().get(0).getTenantName());
    }

    @Override
    public NCLoginUserVO loginByUserTenant(TenantMember member) {
        // 1 校验企业信息
        OperateTenant queryTenant = new OperateTenant();
        queryTenant.setTenantId(member.getTenantId());
        NCResult<OperateTenant> tenantResult = tenantClient.getTenantInfo(queryTenant);
        if (!tenantResult.getSuccess() || tenantResult.getRows() == null || tenantResult.getRows().isEmpty()) {
            throw new NCException("", "不存在该企业！");
        }

        List<TenantMember> exList = memberService.list(new QueryVO<>(member));
        NCUtils.nullOrEmptyThrow(exList, "", "不存在该企业！");
        return getLoginMember(exList.get(0), tenantResult.getRows().get(0).getTenantCode(), tenantResult.getRows().get(0).getTenantName());
    }

    private NCLoginUserVO getLoginMember(TenantMember member, String tenantCode, String tenantName) {
        // 访问令牌
        String accessToken = NCUtils.getUUID();
        // 成员信息令牌 当成员信息令牌改变时，说明成员信息有更新，需重新获取用户信息
        PowerVO powerVO = memberService.getMemberPower(member);

        NCLoginUserVO loginMember = new NCLoginUserVO(powerVO);
        loginMember.setLoginId(member.getMemberId());
        loginMember.setAccount(member.getAccount());
        loginMember.setNickname(StringUtils.isBlank(member.getNickname()) ? member.getAccount() : member.getNickname());
        loginMember.setAvatar(member.getAvatar());
        loginMember.setAccessToken(accessToken);
        loginMember.setUuid(NCUtils.getUUID());
        loginMember.setExpire(expire);
        loginMember.setTenantId(member.getTenantId());
        loginMember.setTenantCode(tenantCode);
        loginMember.setTenantName(tenantName);
        loginMember.setAvatar(member.getAvatar());
        loginMember.setAvatarThumb(member.getAvatarThumb());

        // 初始化用户信息数据权限信息
        redisUtils.set(RedisConstants.MEMBER_LOGIN_INFO + accessToken, loginMember, expire);

        return loginMember;
    }

    @Override
    public void logout(String accessToken) {
        redisUtils.delete(RedisConstants.MEMBER_LOGIN_INFO + accessToken);
    }

    /**
     * 未登陆情况下修改密码
     *
     * @param loginVO
     */
    @Override
    public void updatePwdNoLogin(LoginVO loginVO, String tenantCode) {

        NCUtils.nullOrEmptyThrow(loginVO.getAccount(), new NCException("-1", "账号必填！"));
        NCUtils.nullOrEmptyThrow(loginVO.getPassword(), new NCException("-1", "旧密码必填！"));
        NCUtils.nullOrEmptyThrow(loginVO.getNewPassword(), new NCException("-1", "新密码必填！"));
        // 1 校验企业信息
        OperateTenant queryTenant = new OperateTenant();
        queryTenant.setTenantCode(tenantCode);
        NCResult<OperateTenant> tenantResult = tenantClient.getTenantInfo(queryTenant);
        if (!tenantResult.getSuccess() || tenantResult.getRows() == null || tenantResult.getRows().isEmpty()) {
            throw new NCException("-1", "不存在租户代码为（" + tenantCode +"）的企业！");
        }
        // 2 验证密码是否正确
        QueryVO<TenantMember> queryVO = new QueryVO<>(new TenantMember());
        queryVO.getData().setAccount(loginVO.getAccount());
        queryVO.getData().setTenantId(tenantResult.getRows().get(0).getTenantId());
        List<TenantMember> list = memberService.list(queryVO);
        if (list.isEmpty()) {
            throw new NCException("", "用户密码错误！");
        }
        TenantMember user = list.get(0);
        if (!user.getPassword().equals(PasswordUtil.md5(loginVO.getPassword(), user.getSalt()))) {
            throw new NCException("-1", "密码错误！请重新输入！");
        }

        // 更新密码
        memberService.updatePwd(user.getUserId(), loginVO.getPassword(), loginVO.getNewPassword());
    }
}
