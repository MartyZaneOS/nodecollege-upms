import http from '../utils/request'
// 注册企业（租户）
export function registerTenant (data) {
  return http.post('operateApi', '/userCenter/registerTenant', data)
}
// 查询个人信息
export function getUserInfo (data) {
  return http.post('operateApi', '/userCenter/getUserInfo', data)
}
// 修改个人信息
export function updateUserInfo (data) {
  return http.post('operateApi', '/userCenter/updateUserInfo', data)
}
// 查询个人的所有租户信息
export function getTenantList (data) {
  return http.post('operateApi', '/userCenter/getTenantList', data)
}
// 设置并切换默认登陆企业
export function setDefaultTenant (data) {
  return http.post('operateApi', '/userCenter/setDefaultTenant', data)
}
// 上传头像
export function uploadAvatar (data) {
  return http.post('operateApi', '/userCenter/uploadAvatar', data)
}

// 查询用户租户申请列表
export function getTenantApplyListByUserId (data) {
  return http.post('operateApi', '/tenantApply/getTenantApplyListByUserId', data)
}
// 添加租户申请
export function addTenantApply (data) {
  return http.post('operateApi', '/tenantApply/addTenantApply', data)
}

// 根据昵称查询用户信息
export function getUserListByNickname (data) {
  return http.post('operateApi', '/user/getUserListByNickname', data)
}
