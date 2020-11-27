package com.nodecollege.cloud.common.model.vo;

import com.nodecollege.cloud.common.model.po.OperateAppApi;
import lombok.Data;

import java.util.*;

/**
 * @author LC
 * @date 2020/8/24 20:56
 */
@Data
public class AppApiTreeVO {

    // 类型 1-APP，2-controller,3-api
    private Integer type;
    // app
    private String appName;
    // 父级代码
    private String parentCode;
    // 代码
    private String code;
    // 名称
    private String name;
    // 请求方法
    private String method;
    // 模块名称
    private String modularName;
    // 模块描述
    private String desc;
    // 访问授权 0-都可以访问，1-登录访问，2-授权访问
    private String accessAuth;
    // 状态 1-正常，2-该接口后期删除，-1-已删除
    private Integer state;
    // 子节点
    private List<AppApiTreeVO> children;

    public static List<AppApiTreeVO> getTree(List<OperateAppApi> apiList) {
        List<AppApiTreeVO> res = new ArrayList<>();
        Map<String, Integer> exMap = new HashMap<>();
        for (int i = 0; i < apiList.size(); i++) {
            AppApiTreeVO app = new AppApiTreeVO();
            AppApiTreeVO con = new AppApiTreeVO();
            AppApiTreeVO api = new AppApiTreeVO();
            app.setType(1);
            app.setAppName(apiList.get(i).getApplicationName());
            app.setParentCode("0");
            app.setCode(apiList.get(i).getApplicationName());
            app.setName(apiList.get(i).getApplicationName());

            con.setType(2);
            con.setAppName(apiList.get(i).getApplicationName());
            con.setParentCode(apiList.get(i).getApplicationName());
            con.setCode(apiList.get(i).getControllerName());
            con.setName(apiList.get(i).getControllerName());

            api.setType(3);
            api.setAppName(apiList.get(i).getApplicationName());
            api.setParentCode(apiList.get(i).getControllerName());
            api.setCode(apiList.get(i).getApiUrl());
            api.setName(apiList.get(i).getApiUrl());
            api.setMethod(apiList.get(i).getMethod());

            api.setModularName(apiList.get(i).getModularName());
            api.setDesc(apiList.get(i).getDescription());
            api.setAccessAuth(apiList.get(i).getAccessAuth());
            api.setState(apiList.get(i).getState());
            if (!exMap.containsKey(app.getAppName() + app.getCode())) {
                res.add(app);
                exMap.put(app.getAppName() + app.getCode(), 1);
            }
            if (!exMap.containsKey(con.getAppName() + con.getCode())) {
                res.add(con);
                exMap.put(con.getAppName() + con.getCode(), 1);
            }
            res.add(api);
        }
        return getTree(res, "0", "");
    }

    private static List<AppApiTreeVO> getTree(List<AppApiTreeVO> appApiList, String parentCode, String appName) {
        List<AppApiTreeVO> appApiTreeList = new ArrayList<>();
        Iterator<AppApiTreeVO> appApiIterator = appApiList.iterator();
        while (appApiIterator.hasNext()) {
            AppApiTreeVO appApi = appApiIterator.next();
            if (appApi.getParentCode().equals(parentCode)) {
                if (appApi.getType() == 1) {
                    appApiTreeList.add(appApi);
                    appApiIterator.remove();
                } else if (appApi.getAppName().equals(appName)) {
                    appApiTreeList.add(appApi);
                    appApiIterator.remove();
                }
            }
        }
        if (appApiTreeList.isEmpty()) {
            return null;
        }
        Collections.sort(appApiTreeList, Comparator.comparing(AppApiTreeVO::getCode));
        for (AppApiTreeVO appApiTreeVO : appApiTreeList) {
            appApiTreeVO.setChildren(getTree(appApiList, appApiTreeVO.getCode(), appApiTreeVO.getAppName()));
        }
        return appApiTreeList;
    }
}
