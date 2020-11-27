import http from '../utils/request'

export function getAdminList (data) {
  return http.post('upmsApi', '/admin/admin/getAdminList', data)
}

export function getApiTree (data) {
  return http.post('upmsApi', '/api/getApiTree', data)
}
