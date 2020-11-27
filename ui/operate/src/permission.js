import router from './router'
import {ADMIN_INFO} from 'common'
import notification from 'ant-design-vue/es/notification'

const defaultTitle = '运营运维平台'
router.beforeEach((to, from, next) => {
  document.title = (to.meta.title ? to.meta.title : defaultTitle) + '-节点学院'
  if (to.path) {
    if (window._hmt) {
      window._hmt.push(['_trackPageview', to.fullPath])
    }
  }
  if (to.fullPath.indexOf('/admin/login') >= 0) {
    next()
  } else {
    let adminInfo = JSON.parse(sessionStorage.getItem(ADMIN_INFO))
    if (adminInfo && adminInfo.accessToken) {
      next()
    } else {
      notification.error({message: '登录失效，即将跳转到登录页面'})
      setTimeout(() => { next({path: '/admin/login', query: {redirect: to.fullPath}}) }, 1500)
    }
  }
})
