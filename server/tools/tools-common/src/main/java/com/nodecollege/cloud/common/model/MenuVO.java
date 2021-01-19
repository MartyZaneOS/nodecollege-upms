package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.util.*;

/**
 * 按钮树
 *
 * @author LC
 * @date 20:57 2020/8/17
 **/
@Data
public class MenuVO implements Comparable<MenuVO> {
    // 主键
    private Long id;
    // 产品代码
    private String productCode;
    // 菜单代码 唯一
    private String menuCode;
    // 菜单名称
    private String menuName;
    // 导航平台 0-不生成导航菜单，1-pc端导航，2-移动端导航
    private Integer navPlatform;
    // 菜单类型 0-分类导航，1-菜单页面，2-查询类按钮，3-操作类按钮
    private Integer menuType;
    // 菜单图标
    private String menuIcon;
    // 父级菜单代码
    private String parentCode;
    // 排序号
    private Integer num;
    // 页面代码 menuType in (1)时有效
    private String pageCode;
    // 页面访问路径 menuType in (1)时有效
    private String pagePath;
    // 服务名 menuType in (2,3)时有效
    private String appName;
    // 接口地址 menuType in (2,3)时有效
    private String apiUrl;
    // 数据权限 menuType in (2,3)时有效
    private Integer dataPower;
    // 机构代码列表 menuType in (2,3)时有效 为null时，只能操作用户自己的数据
    private Set<String> orgCodeList = new HashSet<>();
    // 子集
    private List<MenuVO> children;

    public MenuVO() {
    }

    public static List<MenuVO> getMenuTree(List<MenuVO> menuList) {
        Map<Integer, List<MenuVO>> navMap = new HashMap<>();
        for (MenuVO menuVO : menuList) {
            if (!navMap.containsKey(menuVO.getNavPlatform())) {
                navMap.put(menuVO.getNavPlatform(), new ArrayList<>());
            }
            navMap.get(menuVO.getNavPlatform()).add(menuVO);
        }
        List<MenuVO> res = new ArrayList<>();
        for (List<MenuVO> navList : navMap.values()) {
            Set<String> allCode = new HashSet<>();
            navList.forEach(item -> allCode.add(item.getMenuCode()));

            Set<String> topParent = new HashSet<>();
            for (int i = 0; i < navList.size(); i++) {
                String parentCode = navList.get(i).getParentCode();
                if (!topParent.contains(parentCode) && !allCode.contains(parentCode)) {
                    topParent.add(parentCode);
                }
            }
            List<MenuVO> orgTreeList = new ArrayList<>();
            for (String parentCode : topParent) {
                orgTreeList.addAll(getMenuTree(navList, parentCode));
            }
            res.addAll(orgTreeList);
        }
        return res;
    }

    /**
     * 获取menuTree
     */
    public static List<MenuVO> getMenuTree(List<MenuVO> menuList, String parentCode) {
        List<MenuVO> menuTreeList = new ArrayList<>();
        Iterator<MenuVO> menuIterator = menuList.iterator();
        while (menuIterator.hasNext()) {
            MenuVO menu = menuIterator.next();
            if (menu.getParentCode().equals(parentCode)) {
                menuTreeList.add(menu);
                menuIterator.remove();
            }
        }
        Collections.sort(menuTreeList);
        for (MenuVO menuTreeVO : menuTreeList) {
            menuTreeVO.setChildren(getMenuTree(menuList, menuTreeVO.getMenuCode()));
        }
        if (menuTreeList.isEmpty()) {
            return null;
        }
        return menuTreeList;
    }

    @Override
    public int compareTo(MenuVO o) {
        return this.getNum() != null && o.getNum() != null ? this.getNum().compareTo(o.getNum()) : 0;
    }
}
