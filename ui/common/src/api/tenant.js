import http from '../utils/request'

export function login (data) {
  return http.post('tenantApi', '/member/login', data)
}
export function loginByUserTenant (data) {
  return http.post('tenantApi', '/member/loginByUserTenant', data)
}
export function logout (data) {
  return http.post('tenantApi', '/member/logout', data)
}

// 不用登陆修改用户密码
export function updatePwdNoLogin (data) {
  return http.post('tenantApi', '/member/updatePwdNoLogin', data)
}
// 修改用户默认设置
export function changeDefaultOption (data) {
  return http.post('tenantApi', '/memberCenter/changeDefaultOption', data)
}
// 修改密码
export function updatePwd (data) {
  return http.post('tenantApi', '/memberCenter/updatePwd', data)
}
