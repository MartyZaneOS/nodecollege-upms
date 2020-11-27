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

// 获取我的朋友列表
export function getMyFriendList (data) {
  return http.post('operateApi', '/myFriend/getMyFriendList', data)
}
// 获取加我好友的请求列表
export function getNewFriendList (data) {
  return http.post('operateApi', '/myFriend/getNewFriendList', data)
}
// 获取被我拒绝的请求列表
export function getNotAgreeFriendList (data) {
  return http.post('operateApi', '/myFriend/getNotAgreeFriendList', data)
}
// 获取被我加入黑名单的好友列表
export function getBlackList (data) {
  return http.post('operateApi', '/myFriend/getBlackList', data)
}
// 根据昵称查询用户信息
export function getUserListByNickname (data) {
  return http.post('operateApi', '/myFriend/getUserListByNickname', data)
}
// 添加好友
export function addFriend (data) {
  return http.post('operateApi', '/myFriend/addFriend', data)
}
// 处理好友请求
export function handleRequest (data) {
  return http.post('operateApi', '/myFriend/handleRequest', data)
}
// 修改好友信息
export function updateFriend (data) {
  return http.post('operateApi', '/myFriend/updateFriend', data)
}
// 删除好友
export function delFriend (data) {
  return http.post('operateApi', '/myFriend/delFriend', data)
}
// 添加到黑名单
export function addBlack (data) {
  return http.post('operateApi', '/myFriend/addBlack', data)
}
// 移出黑名单
export function delBlack (data) {
  return http.post('operateApi', '/myFriend/delBlack', data)
}

// 添加配置列表
export function addConfig (data) {
  return http.post('operateApi', '/config/addConfig', data)
}
// 编辑配置列表
export function editConfig (data) {
  return http.post('operateApi', '/config/editConfig', data)
}
