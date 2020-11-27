package com.nodecollege.cloud.common.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用返回结构
 *
 * @author LC
 * @date 2019/6/13 16:16
 */
@Data
public class NCResult<T> {

    private String requestId;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 代码
     */
    private String code;
    /**
     * 消息
     */
    private String message;
    /**
     * 数据数量
     */
    private Long total;
    /**
     * 业务数据
     */
    private List<T> rows;

    /**
     * 无参构造函数
     */
    public NCResult() {
    }

    /**
     * 基础构造函数
     *
     * @param success
     * @param code
     * @param message
     * @param rows
     */
    public NCResult(boolean success, String code, String message, List<T> rows, Number total) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.total = total.longValue();
        this.rows = rows;
    }

    public static NCResult ok() {
        return ok(States.SUCCESS.getMessage(), new ArrayList());
    }

    public static <T> NCResult ok(T data) {
        List<T> list = new ArrayList<>();
        list.add(data);
        return ok(States.SUCCESS.getMessage(), list);
    }

    public static <T> NCResult ok(Iterable<T> data) {
        List<T> results = new ArrayList<>();
        data.forEach(v -> results.add(v));
        return ok(States.SUCCESS.getMessage(), results);
    }

    public static <T> NCResult ok(List<T> data) {
        return ok(States.SUCCESS.getMessage(), data);
    }

    public static <T> NCResult ok(List<T> data, int total) {
        return ok(data, Long.valueOf(total));
    }

    public static <T> NCResult ok(List<T> data, Long total) {
        return ok(States.SUCCESS.getMessage(), data, total);
    }

    public static NCResult ok(String message) {
        return ok(message, new ArrayList());
    }

    public static NCResult ok(String message, List data) {
        return ok(message, data, Long.valueOf(data != null ? data.size() : 0));
    }

    public static NCResult ok(String message, List data, Long total) {
        return new NCResult(true, States.SUCCESS.getCode(), message, data, total);
    }

    public static NCResult error() {
        return error(States.ERROR.getMessage());
    }

    public static NCResult error(String message) {
        return error(States.ERROR.getCode(), message);
    }


    public static NCResult error(String code, String message) {
        return error(code, message, new ArrayList());
    }

    public static NCResult error(String message, List data) {
        return error("-1", message, data);
    }

    public static NCResult error(String code, String message, List data) {
        return new NCResult(false, code, message, data, data.size());
    }

    public enum States {
        /**
         * 请求成功
         */
        SUCCESS("0", "请求成功！"),
        /**
         * 请求失败
         */
        ERROR("-1", "请求失败！");

        private final String code;
        private final String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        States(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static String toJson() {
            JSONArray jsonArray = new JSONArray();
            NCResult.States[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                NCResult.States e = var1[var3];
                JSONObject object = new JSONObject();
                object.put("code", e.getCode());
                object.put("value", e.getMessage());
                jsonArray.add(object);
            }

            return jsonArray.toString();
        }
    }

}
