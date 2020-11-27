import http from '../utils/request'
// 查询我的群组列表
export function getGroupList (data) {
  return http.post('chatApi', '/chat/myGroup/getGroupList', data)
}
// 创建群组
export function addMyGroup (data) {
  return http.post('chatApi', '/chat/myGroup/addMyGroup', data)
}
