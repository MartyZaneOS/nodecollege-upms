import axios from 'axios'
import notification from 'ant-design-vue/es/notification'
// import {ADMIN_INFO} from 'common'

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL, // api base_url
  timeout: 60000 // 请求超时时间
})

const err = (error) => {
  if (error.response) {
    const data = error.response.data
    if (error.response.status === 403) {
      notification.error({
        message: 'Forbidden',
        description: data.message
      })
    }
    if (error.response.status === 401 && !(data.result && data.result.isLogin)) {
      notification.error({
        message: 'Unauthorized',
        description: 'Authorization verification failed'
      })
    }
  }
  return Promise.reject(error)
}

// request interceptor
service.interceptors.request.use(config => {
  config.headers['ACCESS-SOURCE'] = 'nc-nginx'
  // let adminInfo = JSON.parse(sessionStorage.getItem(ADMIN_INFO))
  // if (adminInfo && adminInfo.accessToken) {
  //   config.headers['ADMIN-ACCESS-TOKEN'] = adminInfo.accessToken // 让每个请求携带自定义 token 请根据实际情况自行修改
  //   config.headers['ADMIN-UUID'] = adminInfo.uuid // 让每个请求携带自定义 token 请根据实际情况自行修改
  // }
  return config
}, err)

// response interceptor
service.interceptors.response.use((response) => {
  if (!response.data.success) {
    if (response.data.code === '80000000') {
      notification.error({message: '登录失效，即将跳转到登录页面'})
      setTimeout(() => { window.location.href = '/admin/login' }, 1500)
      return response.data
    } else {
      notification.error({
        message: response.data.message
      })
    }
  }
  return response.data
}, err)

const prefixTable = {
  upmsApi: '/upmsApi',
  operateApi: '/operateApi',
  logApi: '/logApi',
  syncApi: '/syncApi'
}

export default {
  // installer as VueAxios,
  // service as axios,
  get (prefix, url, params) {
    return service({
      method: 'get',
      url: (prefixTable[prefix] || '') + url,
      params
    })
  },
  post (prefix, url, data) {
    return service({
      method: 'post',
      url: (prefixTable[prefix] || '') + url,
      data
    })
  },
  formPost (prefix, url, data) {
    return service({
      method: 'post',
      url: (prefixTable[prefix] || '') + url,
      data,
      transformRequest: [function (data) {
        let ret = ''
        for (let it in data) {
          ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
        }
        return ret
      }],
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }
}
