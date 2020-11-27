import router from './router'
import {MEMBER_INFO} from 'common'
import notification from 'ant-design-vue/es/notification'

const defaultTitle = '租户管理'
router.beforeEach((to, from, next) => {
  document.title = (to.meta.title ? to.meta.title : defaultTitle) + '-节点学院'
  if (to.path) {
    if (window._hmt) {
      window._hmt.push(['_trackPageview', to.fullPath])
    }
  }
  if (to.fullPath.indexOf('/tenant/login') === 0) {
    next()
  } else {
    let memberInfo = JSON.parse(sessionStorage.getItem(MEMBER_INFO))
    if (memberInfo && memberInfo.accessToken) {
      // 判断当前的token是否存在
      next()
    } else {
      notification.error({message: '登录失效，即将跳转到登录页面'})
      setTimeout(() => { next({path: '/tenant/login', query: {redirect: to.fullPath}}) }, 1500)
    }
  }
})
