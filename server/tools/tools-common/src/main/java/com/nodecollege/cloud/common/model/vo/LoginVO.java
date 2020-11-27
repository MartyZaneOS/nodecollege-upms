package com.nodecollege.cloud.common.model.vo;

import lombok.Data;

/**
 * 管理员登陆vo
 * @author LC
 * @date 2019/12/1 12:16
 */
@Data
public class LoginVO {

    /**
     * 登陆方式
     * 0-用户名密码
     * 1-手机号短信验证码
     */
    private Integer type;
    /**
     * 注册来源
     * 0-个人注册
     * 1-管理员添加
     */
    private Integer source;
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 新密码
     */
    private String newPassword;
    /**
     * 电话
     */
    private String telephone;
    /**
     * 短信验证码
     */
    private String smsCode;
    /**
     * 滑块验证码
     */
    private String sliderCert;
    /**
     * 图片验证码
     */
    private String imageCert;
    /**
     * token
     */
    private String token;

    private String rsaTag;
}
