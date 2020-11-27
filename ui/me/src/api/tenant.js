import http from '../utils/request'

export function loginByUserTenant (data) {
  return http.post('tenantApi', '/member/loginByUserTenant', data)
}
