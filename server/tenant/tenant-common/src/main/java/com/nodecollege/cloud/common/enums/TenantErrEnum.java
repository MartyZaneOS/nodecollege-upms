package com.nodecollege.cloud.common.enums;

import com.nodecollege.cloud.common.model.CodeMessage;
import org.springframework.util.StringUtils;

/**
 * @author LC
 * @date 20:45 2019/11/27
 **/
public enum TenantErrEnum implements CodeMessage {
    ADMIN_ACCOUNT_NULL_ERROR("80100001", "管理员账号必填！"),
    NO_SUCH_ALGORITHM_ERROR("80100002", "不存在的加密方式！"),
    INVALID_KEY_ERROR("80100002", "生成签名失败！"),
    IO_ERROR("80100002", "IO异常！"),
    GET_KEY_PAIR_ERROR("80100002", "生成密钥对失败！"),
    SIGN_ERROR("80100002", "用私钥对信息生成数字签名失败！"),
    VERIFY_ERROR("80100002", "公钥校验数字签名失败！"),
    DECRYPT_BY_PRIVATE_KEY_ERROR("80100002", "私钥解密失败！"),
    DECRYPT_BY_PUBLIC_KEY_ERROR("80100002", "公钥解密失败！"),
    ENCRYPT_BY_PUBLIC_KEY_ERROR("80100002", "公钥加密失败！"),
    ENCRYPT_BY_PRIVATE_KEY_ERROR("80100002", "私钥加密失败！"),

    ;

    private String message;
    private String code;

    public static TenantErrEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        } else {
            TenantErrEnum[] errorEnums = values();
            for (TenantErrEnum errorEnum : errorEnums) {
                if (errorEnum.getCode().equals(code)) {
                    return errorEnum;
                }
            }
            return null;
        }
    }

    public static String getMsgByCode(String code) {
        return getByCode(code).getMessage();
    }

    /**
     * 构造函数
     *
     * @param code    String
     * @param message String
     */
    TenantErrEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
