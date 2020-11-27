import http from '../utils/request'

export function loginByUserTenant (data) {
  return http.post('tenantApi', '/member/loginByUserTenant', data)
}

// 查询用户组织机构树
export function getOrgTree (data) {
  return http.post('tenantApi', '/org/getOrgTree', data)
}
// 添加用户组织机构
export function addOrg (data) {
  return http.post('tenantApi', '/org/addOrg', data)
}
// 编辑用户组织机构
export function editOrg (data) {
  return http.post('tenantApi', '/org/editOrg', data)
}
// 删除用户组织机构
export function delOrg (data) {
  return http.post('tenantApi', '/org/delOrg', data)
}
// 根据机构信息查询用户信息
export function getRoleListByOrg (data) {
  return http.post('tenantApi', '/role/getRoleListByOrg', data)
}

// 查询用户列表
export function getMemberList (data) {
  return http.post('tenantApi', '/member/getMemberList', data)
}
// 邀请用户
export function inviteMember (data) {
  return http.post('tenantApi', '/member/inviteMember', data)
}
// 编辑用户
export function editMember (data) {
  return http.post('tenantApi', '/member/editMember', data)
}
// 删除用户
export function delMember (data) {
  return http.post('tenantApi', '/member/delMember', data)
}
// 重置用户密码
export function resetMemberPwd (data) {
  return http.post('tenantApi', '/member/resetPwd', data)
}
// 根据用户信息查询组织机构列表
export function getOrgTreeByMember (data) {
  return http.post('tenantApi', '/org/getOrgTreeByMember', data)
}
// 绑定用户机构
export function bindMemberOrg (data) {
  return http.post('tenantApi', '/member/bindMemberOrg', data)
}
// 查询角色列表根据用户信息
export function getRoleListByMember (data) {
  return http.post('tenantApi', '/role/getRoleListByMember', data)
}
// 查询管理员授权信息
export function getMemberPower (data) {
  return http.post('tenantApi', '/member/getMemberPower', data)
}

// 查询角色列表
export function getRoleList (data) {
  return http.post('tenantApi', '/role/getRoleList', data)
}
// 添加角色
export function addRole (data) {
  return http.post('tenantApi', '/role/addRole', data)
}
// 编辑角色
export function editRole (data) {
  return http.post('tenantApi', '/role/editRole', data)
}
// 删除角色
export function delRole (data) {
  return http.post('tenantApi', '/role/delRole', data)
}
// 查询角色菜单
export function getRoleMenuList (data) {
  return http.post('tenantApi', '/role/getRoleMenuList', data)
}
// 绑定角色菜单
export function bindRoleMenu (data) {
  return http.post('tenantApi', '/role/bindRoleMenu', data)
}
// 查询机构列表根据角色信息
export function getOrgTreeByRole (data) {
  return http.post('tenantApi', '/org/getOrgTreeByRole', data)
}
// 绑定角色组织机构
export function bindRoleOrg (data) {
  return http.post('tenantApi', '/role/bindRoleOrg', data)
}
// 根据机构获取用户列表
export function getMemberListByOrg (data) {
  return http.post('tenantApi', '/member/getMemberListByOrg', data)
}
// 查询用户列表根据机构角色
export function getMemberListByRoleOrg (data) {
  return http.post('tenantApi', '/member/getMemberListByRoleOrg', data)
}
// 添加用户机构角色
export function addMemberOrgRole (data) {
  return http.post('tenantApi', '/member/addMemberOrgRole', data)
}
// 删除用户机构角色
export function delMemberOrgRole (data) {
  return http.post('tenantApi', '/member/delMemberOrgRole', data)
}

// 查询产品菜单树
export function getMenuTree (data) {
  return http.post('tenantApi', '/tenant/getMenuTree', data)
}

// 查询配置列表
export function getConfigList (data) {
  return http.post('tenantApi', '/config/getConfigList', data)
}
// 编辑配置信息
export function editConfig (data) {
  return http.post('tenantApi', '/config/editConfig', data)
}
