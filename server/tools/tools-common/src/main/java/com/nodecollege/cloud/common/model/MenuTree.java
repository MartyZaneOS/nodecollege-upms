package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.util.List;

/**
 * @author LC
 * @date 2020/8/24 21:58
 */
@Data
public class MenuTree {
    // 菜单名称
    private String menuName;
    // 菜单代码
    private String menuCode;
    // 父级代码
    private String parentCode;
    /**
     * 菜单类型
     * 0-分类导航
     * 1-菜单页面
     * 2-查询类按钮
     * 3-操作类按钮
     */
    private Integer menuType;
    // 图标
    private String menuIcon;
    // 路径 资源类型为 功能页面时必填
    private String path;
    /**
     * 打开方式
     * 资源类型为 功能页面时必填
     * 0-单页方式（前端项目中已经集成好的页面）
     * 1-iframe方式（第三方开发的单页应用）
     */
    private Integer openType;
    // 接口服务名称 资源类型为 按钮事件时必填
    private String appName;
    // 接口地址 资源类型为 按钮事件时必填
    private String apiUrl;
    // 排序号
    private Integer num;
    // 状态 -1-已删除，0-不可删除，1-可删除，2-冻结，3-不推荐使用
    private Integer state;
    // 子节点
    private List<MenuTree> children;
}
