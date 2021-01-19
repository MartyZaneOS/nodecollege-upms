package com.nodecollege.cloud.common.enums;

import com.nodecollege.cloud.common.model.CodeMessage;
import org.springframework.util.StringUtils;

/**
 * 异常枚举
 *
 * @author zhaojy
 * @date 2018-07-12
 */
public enum ErrorEnum implements CodeMessage {

    OPT_DATA_BASE_ERROR("99999998", "数据库异常"),
    UNKNOWN_ERROR("99999999", "系统异常，请稍后再试"),
    LOGIN_TIME_OUT("80000000", "登录超时，请重新登录"),
    VISIT_ERROR("80000001", "无权访问！"),
    ILLEGAL_OPERATION("80000002", "非法操作"),
    FEIGN_SERVICE_ERROR("80000003", "请求服务不可用"),
    REQUEST_PARAM_EMPTY("80000004", "请求参数为空"),
    REQUEST_PARAM_ERROR("80000005", "请求数据不正确"),
    REQUEST_METHOD_ERROR("80000006", "请求方法方式不正确，请检查"),
    RETURN_DATA_NOT_FOUND("80000007", "返回数据不存在"),
    DATA_NOT_FOUND("80000008", "未查询到相关信息，请重新输入搜索条件"),
    FILE_SIZE_ERROR("80000009", "当前仅支持20Mb以内的电子文件，请重新上传"),
    SMS_ERROR("80000010", "短信推送服务不可用,请重试"),
    PHONE_FORMAT_ERROR("80000011", "手机号格式错误"),
    PHONE_INVALID_ERROR("80000012", "短信验证码已失效，请重新发送"),
    PHONE_INPUT_ERROR("80000013", "短信验证码输入错误，请重试"),
    IMG_VERIFICATION_ERROR("80000014", "图片验证码输入错误"),
    FILE_TYPE_ERROR("80000015", "文件类型必传！"),
    FILE_TYPE_0_ERROR("80000016", "文件类型为0时，imgBase64必传！"),
    FILE_TYPE_1_ERROR("80000017", "文件类型为1时，imgUrl必传！"),
    BEAN_2_MAP_ERROR("80000018", "javaBean转Map失败！"),

    ;

    private String message;
    private String code;

    public static ErrorEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        } else {
            ErrorEnum[] errorEnums = values();
            for (ErrorEnum errorEnum : errorEnums) {
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
    ErrorEnum(String code, String message) {
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
