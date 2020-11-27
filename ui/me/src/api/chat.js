import http from '../utils/request'
// 查询我的群组列表
export function getGroupList (data) {
  return http.post('chatApi', '/chat/myGroup/getGroupList', data)
}
// 创建群组
export function addMyGroup (data) {
  return http.post('chatApi', '/chat/myGroup/addMyGroup', data)
}

// 获取我的朋友列表
export function getMyFriendList (data) {
  return http.post('chatApi', '/myFriend/getMyFriendList', data)
}
// 获取加我好友的请求列表
export function getNewFriendList (data) {
  return http.post('chatApi', '/myFriend/getNewFriendList', data)
}
// 获取被我拒绝的请求列表
export function getNotAgreeFriendList (data) {
  return http.post('chatApi', '/myFriend/getNotAgreeFriendList', data)
}
// 获取被我加入黑名单的好友列表
export function getBlackList (data) {
  return http.post('chatApi', '/myFriend/getBlackList', data)
}
// 添加好友
export function addFriend (data) {
  return http.post('chatApi', '/myFriend/addFriend', data)
}
// 处理好友请求
export function handleRequest (data) {
  return http.post('chatApi', '/myFriend/handleRequest', data)
}
// 修改好友信息
export function updateFriend (data) {
  return http.post('chatApi', '/myFriend/updateFriend', data)
}
// 删除好友
export function delFriend (data) {
  return http.post('chatApi', '/myFriend/delFriend', data)
}
// 添加到黑名单
export function addBlack (data) {
  return http.post('chatApi', '/myFriend/addBlack', data)
}
// 移出黑名单
export function delBlack (data) {
  return http.post('chatApi', '/myFriend/delBlack', data)
}
