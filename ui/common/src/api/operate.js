import http from '../utils/request'

// 查询角色列表根据管理员信息
export function getPublicKey (data) {
  return http.post('operateApi', '/common/getPublicKey', data)
}

// 查询角色列表根据管理员信息
export function adminLogin (data) {
  return http.post('operateApi', '/admin/login', data)
}
// 查询管理员授权信息
export function adminLogout (data) {
  return http.post('operateApi', '/admin/logout', data)
}
// 修改密码
export function updatePassword (data) {
  return http.post('operateApi', '/admin/centre/updatePassword', data)
}
// 不用登陆修改密码
export function updatePwdNoLogin (data) {
  return http.post('operateApi', '/admin/updatePwdNoLogin', data)
}
// 修改管理员默认设置
export function changeDefaultOption (data) {
  return http.post('operateApi', '/admin/centre/changeDefaultOption', data)
}

// 查询角色列表根据管理员信息
export function register (data) {
  return http.post('operateApi', '/user/register', data)
}
// 查询角色列表根据管理员信息
export function userLogin (data) {
  return http.post('operateApi', '/user/login', data)
}
// 查询管理员授权信息
export function userLogout (data) {
  return http.post('operateApi', '/user/logout', data)
}
// 不用登陆修改用户密码
export function updateUserPwdNoLogin (data) {
  return http.post('operateApi', '/user/updatePwdNoLogin', data)
}
// 查询个人的所有租户信息
export function getTenantList (data) {
  return http.post('operateApi', '/userCenter/getTenantList', data)
}
// 修改用户默认设置
export function changeUserDefaultOption (data) {
  return http.post('operateApi', '/userCenter/changeUserDefaultOption', data)
}
