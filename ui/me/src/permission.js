import router from './router'
import {USER_INFO} from 'common'
import notification from 'ant-design-vue/es/notification'

const defaultTitle = '个人中心'
router.beforeEach((to, from, next) => {
  document.title = (to.meta.title ? to.meta.title : defaultTitle) + '-节点学院'
  if (to.path) {
    if (window._hmt) {
      window._hmt.push(['_trackPageview', to.fullPath])
    }
  }
  if (to.fullPath.indexOf('/me/login') === 0) {
    console.log('用户登录！')
    next()
  } else {
    let userInfo = JSON.parse(sessionStorage.getItem(USER_INFO))
    if (userInfo && userInfo.accessToken) {
      // 判断当前的token是否存在
      next()
    } else {
      notification.error({message: '登录失效，即将跳转到登录页面'})
      setTimeout(() => { next({path: '/me/login', query: {redirect: to.fullPath}}) }, 1500)
    }
  }
})
