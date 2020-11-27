package com.nodecollege.cloud.common.model;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author LC
 * @date 2020/9/11 21:04
 */
@Data
public class PowerVO {
    // 显示所有机构数据
    private Boolean showAllOrg;
    // 显示所有角色数据
    private Boolean showAllRole;
    // 默认机构代码 showAllOrg=false有效
    private String defaultOrgCode;
    // 默认角色代码 showAllRole=false有效
    private String defaultRoleCode;
    // 拥有的所有角色列表 用于顶部导航切换角色时展示 showAllRole == false
    private Set<CodeName> roleList;
    // 拥有的所有机构列表 用于顶部导航切换机构时展示 showAllOrg == false
    private Set<CodeName> orgList;
    // 角色对应机构列表 用于showAllRole = false && showAllOrg == false 时切换机构，选择该机构对应的角色
    private Map<String, Set<CodeName>> roleOrgMap;
    // 机构对应角色列表 用于showAllOrg = false && showAllRole == false 时切换角色，选择该角色对应的机构
    private Map<String, Set<CodeName>> orgRoleMap;
    // 菜单树
    private List<MenuVO> menuTree;

    @Data
    public static class CodeName {
        // 代码
        private String code;
        // 名称
        private String name;

        public CodeName() {
        }

        public CodeName(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CodeName codeName = (CodeName) o;
            return code.equals(codeName.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, name);
        }
    }
}

